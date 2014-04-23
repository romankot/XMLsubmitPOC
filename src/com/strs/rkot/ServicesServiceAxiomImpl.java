package com.strs.rkot;

import com.streamserve.schemas.webservice.types.servicedirectory._1.AttributeType;
import java.util.ArrayList;
import java.util.List;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import com.streamserve.schemas.webservice.interfaces.servicedirectory._1.GetServicesRequestType;
import com.streamserve.schemas.webservice.interfaces.servicedirectory._1.GetServicesResponseType;
import com.streamserve.schemas.webservice.types.query.servicedirectory._1.ConditionCollectionType;
import com.streamserve.schemas.webservice.types.query.servicedirectory._1.ConditionType;
import com.streamserve.schemas.webservice.types.query.servicedirectory._1.FilterClauseType;
import com.streamserve.schemas.webservice.types.servicedirectory._1.AttributeEnumType;
import com.streamserve.schemas.webservice.types.servicedirectory._1.ExtendedAttributeEnumType;
import com.streamserve.schemas.webservice.types.servicedirectory._1.ServiceType;
import com.streamserve.schemas.webservice.types.servicedirectory._1.ValueCollectionType;
import java.io.IOException;
import javax.xml.transform.TransformerException;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapMessage;
import com.streamserve.schemas.webservice.interfaces.servicedirectory._1.ObjectFactory;
import com.streamserve.schemas.webservice.types.query.servicedirectory._1.SelectClauseType;
import com.streamserve.schemas.webservice.types.servicedirectory._1.AttributeIDCollectionType;
import com.streamserve.schemas.webservice.types.servicedirectory._1.AttributeIDType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;


public class ServicesServiceAxiomImpl extends WebServiceGatewaySupport implements ServicesService {

	ObjectFactory objectFactory = new ObjectFactory();

	public ServicesServiceAxiomImpl(AxiomSoapMessageFactory messageFactory) {
		super(messageFactory);
	}
        
        void addAttribute( List<AttributeIDType> attribList, AttributeEnumType attribType)
        {
            AttributeIDType attributeID = new AttributeIDType();
            ExtendedAttributeEnumType attribute = new ExtendedAttributeEnumType();
            attribute.setEnumValue(attribType);
            attributeID.setId(attribute);
            attribList.add(attributeID);
        }

	@Override
	public List<Service> getServices(String endpoint) {
		List<Service> lst = new ArrayList<Service>();

		final GetServicesRequestType req = objectFactory.createGetServicesRequestType();
                
                // Select
                final AttributeIDCollectionType attributes = new AttributeIDCollectionType();
                final SelectClauseType select = new SelectClauseType();
                
                addAttribute(attributes.getAttribute(), AttributeEnumType.SERVICE_NAME);
                addAttribute(attributes.getAttribute(), AttributeEnumType.SERVICE_TYPE);
                addAttribute(attributes.getAttribute(), AttributeEnumType.SERVICE_URI);       
                select.setAttributes(attributes);
                req.setSelect(select);
                
                // Filter
		FilterClauseType filter = new FilterClauseType();
		ConditionCollectionType condition = new ConditionCollectionType();
		ConditionType conditionType = new ConditionType();
		ExtendedAttributeEnumType id = new ExtendedAttributeEnumType();
		id.setEnumValue(AttributeEnumType.SERVICE_TYPE);
		conditionType.setId(id);
		ValueCollectionType values = new ValueCollectionType();
		values.getValue().add("Generic");
		conditionType.setValues(values);
		conditionType.setOperatorValue("==");
		condition.getCondition().add(conditionType);
		filter.setConditions(condition);
		req.setFilter(filter);
		GetServicesResponseType services = (GetServicesResponseType) getWebServiceTemplate().marshalSendAndReceive(endpoint, req, new WebServiceMessageCallback() {

			public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
				((SoapMessage) message).setSoapAction("http://schemas.streamserve.com/webservice/servicedirectory/1.0/getservices");
			}
		});

		for (ServiceType serviceType : services.getResult().getServices().getService()) {
			List<AttributeType> attrs = serviceType.getAttributes().getAttribute();
			Service s = new Service();
			for(AttributeType at : attrs) {
				if(at.getId().getEnumValue() == AttributeEnumType.SERVICE_NAME) {
					s.setName(at.getValues().getValue().get(0));
				}
				if(at.getId().getEnumValue() == AttributeEnumType.SERVICE_URI) {
					s.setServiceURI(at.getValues().getValue().get(0));
				}
			}
			lst.add(s);
		}

		return lst;
	}

	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml", ServicesServiceAxiomImpl.class);
		ServicesService srv = (ServicesService) ctx.getBean("servicesService");
		List<Service> services = srv.getServices("http://localhost:2718");
		for(Service s : services) {
			System.out.println(s + " -> " + s.getServiceURI());
		}
	}
}
