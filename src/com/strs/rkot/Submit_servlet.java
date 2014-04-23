package com.strs.rkot;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Servlet implementation class Submit_servlet
 */
@WebServlet("/Submit_servlet")
public class Submit_servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Submit_servlet() {
        super();
        
    
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
			ApplicationContext ctx = new ClassPathXmlApplicationContext("/com/strs/rkot/applicationContext.xml", Submit_servlet.class);
			ServicesService serviceService = (ServicesService) ctx.getBean("servicesService");
			JobSubmitService srv = (JobSubmitService) ctx.getBean("jobSubmitService");
			
			String filePath= request.getParameter("filePath");
			
			List<Job> lst = new ArrayList<Job>();
			Job j = new Job();
			j.setExternalJobId("testExternalId");
			j.setSubmitType("FireAndForget");
			
			j.setFilePath(filePath );
			lst.add(j);
			
			List<Service> lst_ser = serviceService.getServices("http://localhost:2718");			

			List<JobSubmitResult> resp = srv.submitJobs(lst, lst_ser.get(0).getServiceURI(), "http://localhost:2718");
			for(JobSubmitResult o : resp) {
				System.out.println(o);
			}
			
			if (resp.get(0).getId() != null)
			{
				response.sendRedirect("SubmitSuccess.jsp");
			}
			else
			{
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.html");
	            PrintWriter out= response.getWriter();
	            out.println("<font color=red>Something is wrong.</font>");
	            rd.include(request, response);
			}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
