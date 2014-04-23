package com.strs.rkot;


import com.streamserve.schemas.webservice.interfaces.jobsubmitcontent.mtom._1.SubmitJobsContentResponseType;
import com.streamserve.schemas.webservice.types.content.mtom._1.JobContentType;
import com.streamserve.schemas.webservice.types.content.mtom._1.JobSubmitContentResponseType;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMText;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.axiom.AxiomSoapMessage;


public class JobSubmitServiceAxiomImpl extends WebServiceGatewaySupport implements JobSubmitService {

	public JobSubmitServiceAxiomImpl(WebServiceMessageFactory messageFactory) {
		super(messageFactory);
	}

	public List<JobSubmitResult> submitJobs(final List<Job> jobs, final String serviceURI, String endpoint) {
		try {
			@SuppressWarnings("unchecked")
			List<JobSubmitResult> resp = (List<JobSubmitResult>) getWebServiceTemplate().sendAndReceive(endpoint, new WebServiceMessageCallback() {

				public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
					AxiomSoapMessage soapMessage = (AxiomSoapMessage) message;
					SOAPMessage axiomMessage = soapMessage.getAxiomMessage();
					SOAPFactory factory = (SOAPFactory) axiomMessage.getOMFactory();

					createHeader(axiomMessage.getSOAPEnvelope(), factory, serviceURI);
					createBody(axiomMessage.getSOAPEnvelope().getBody(), jobs, factory);

					OMOutputFormat outputFormat = new OMOutputFormat();
					outputFormat.setSOAP11(true);
					outputFormat.setDoOptimize(true);
					soapMessage.setOutputFormat(outputFormat);
					soapMessage.setSoapAction("http://schemas.streamserve.com/webservice/jobsubmitcontent/mtom/1.0/submitjobscontent");
				}

				private void createHeader(SOAPEnvelope env, SOAPFactory factory, String serviceURI) {
					OMNamespace ns = factory.createOMNamespace("http://schemas.xmlsoap.org/ws/2004/08/addressing", "a");
					env.declareNamespace(ns);
					SOAPHeader header = env.getHeader();
					// Action
					OMElement el = factory.createOMElement("Action", ns);
					OMAttribute attr = factory.createOMAttribute("mustUnderstand", ns, "1");
					el.addAttribute(attr);
					el.setText("http://schemas.streamserve.com/webservice/jobsubmitcontent/mtom/1.0/submitjobscontent");
					header.addChild(el);
					// MessageID
					el = factory.createOMElement("MessageID", ns);
					el.setText(String.format("urn:uuid:%s", UUID.randomUUID()));
					header.addChild(el);
					// ReplyTo.Address
					el = factory.createOMElement("ReplyTo", ns);
					OMElement el2 = factory.createOMElement("Address", ns);
					el2.setText("http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous");
					el.addChild(el2);
					header.addChild(el);
					// To
					el = factory.createOMElement("To", ns);
					el.addAttribute(attr);
					el.setText(serviceURI);
					header.addChild(el);
				}

				private void createBody(SOAPBody body, List<Job> jobsList, SOAPFactory factory) {

					OMNamespace ns = factory.createOMNamespace("http://www.w3.org/2001/XMLSchema", "xsd");
					body.declareNamespace(ns);
					ns = factory.createOMNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");
					body.declareNamespace(ns);

					OMElement el = factory.createOMElement(new QName("SubmitJobsContentRequestType"));
					el.declareDefaultNamespace("http://schemas.streamserve.com/webservice/interfaces/jobsubmitcontent/mtom/1.0");
					body.addChild(el);
					OMElement jobs = factory.createOMElement(new QName("jobs"));
					el.addChild(jobs);

					for (Job j : jobsList) {
						OMElement job = factory.createOMElement(new QName("job"));
						jobs.addChild(job);

						OMElement content = factory.createOMElement(new QName("content"));
						OMNamespace bNs = factory.createOMNamespace("http://www.w3.org/2005/05/xmlmime", "b");
						content.declareNamespace(bNs);
						content.addAttribute("contentType", "application/xop+xml", bNs);
						job.addChild(content);

						el = factory.createOMElement(new QName("content"));
						content.addChild(el);

						DataSource dataSource = new FileDataSource(j.getFilePath());
						DataHandler dataHandler = new DataHandler(dataSource);
						OMText text = factory.createOMText(dataHandler, true);
						el.addChild(text);

						el = factory.createOMElement(new QName("submitType"));
						el.setText(j.getSubmitType());
						job.addChild(el);

						el = factory.createOMElement(new QName("externalJobID"));
						el.setText(j.getExternalJobId());
						job.addChild(el);

						if(j.getAttributeId() != null && j.getAttributeId().trim().length() > 0) {
							OMElement attributes = factory.createOMElement(new QName("attributes"));
							job.addChild(attributes);

							OMElement attribute = factory.createOMElement(new QName("attribute"));
							attribute.declareDefaultNamespace("http://schemas.streamserve.com/public/1.0");
							attributes.addChild(attribute);

							OMElement id = factory.createOMElement(new QName("id"));
							id.declareDefaultNamespace("http://schemas.streamserve.com/public/1.0");
							id.setText(j.getAttributeId());
							attribute.addChild(id);

							OMElement values = factory.createOMElement(new QName("values"));
							values.declareDefaultNamespace("http://schemas.streamserve.com/public/1.0");
							attribute.addChild(values);
							OMElement value = factory.createOMElement(new QName("value"));
							value.declareDefaultNamespace("http://schemas.streamserve.com/public/1.0");
							value.setText(j.getAttributeValue());
							values.addChild(value);
						}
					}

				}
			}, new WebServiceMessageExtractor() {

				public Object extractData(WebServiceMessage message) throws IOException, TransformerException {
					List<JobSubmitResult> lst = new ArrayList<JobSubmitResult>();
					AxiomSoapMessage msg = (AxiomSoapMessage) message;
					SoapBody body = msg.getSoapBody();
					SubmitJobsContentResponseType resp = (SubmitJobsContentResponseType) getUnmarshaller().unmarshal(body.getPayloadSource());
					for(JobSubmitContentResponseType r : resp.getJobs().getJob()) {
						JobSubmitResult jsr = new JobSubmitResult();
						jsr.setResultCode(r.getResultCode());
						if(jsr.getResultCode() == 0) {
							jsr.setId(r.getId());
						} else {
							jsr.setDescription(r.getDescription());
						}
						lst.add(jsr);
						
						if (r.getAttachments() == null) 
						{							
						}
						
						else if(!r.getAttachments().getAttachment().isEmpty()) {

							for(JobContentType jct : r.getAttachments().getAttachment()) {
								File file = new File(new File(System.getProperty("java.io.tmpdir")), System.currentTimeMillis() + ".pdf");
								OutputStream out = new FileOutputStream(file);

								out.write(jct.getData().getContent());
								out.flush();
								out.close();

								if(Desktop.isDesktopSupported()) {
									Desktop.getDesktop().open(file);
								}
							}
						}
					}
					return lst;
				}
			});
			return resp;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml", JobSubmitServiceAxiomImpl.class);
		JobSubmitService srv = (JobSubmitService) ctx.getBean("jobSubmitService");
		List<Job> lst = new ArrayList<Job>();
		Job j = new Job();
		j.setExternalJobId("testExternalId");
		j.setSubmitType("FireAndForget");
		j.setFilePath("C:\\download\\and\\samples\\sample.xml");
		lst.add(j);
		List<JobSubmitResult> resp = srv.submitJobs(lst, "http://schemas.streamserve.com/uid/service/externalqueuing/1.0/submitInvoice", "http://localhost:2718");
		for(JobSubmitResult o : resp) {
			System.out.println(o);
		}
	}
}
