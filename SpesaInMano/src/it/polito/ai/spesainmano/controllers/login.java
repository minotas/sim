package it.polito.ai.spesainmano.controllers;

import it.polito.ai.spesainmano.DAO.UserDAO;
import it.polito.ai.spesainmano.DAO.UserDAOImp;
import it.polito.ai.spesainmano.model.User;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

/**
 * Servlet implementation class login
 */
@WebServlet("/login")
public class login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jsonData = request.getParameter("jsondata");
		
		response.setContentType("application/x-json");
        Gson gson = new Gson();
        User user = (User)gson.fromJson(jsonData, User.class);
        String userJson;
        if(user.getUsername().equals("") || user.getPassword().equals("")){
     	   userJson = "Incomplete info";
        }
        else{
	           UserDAO userDao = new UserDAOImp();
	           user = userDao.login(user.getUsername(), user.getPassword());
	          
	           if(user == null){
	        	   userJson = "Login failed";
	        	  
	           }
	           else{
	        	   userJson = gson.toJson(user); 
	        	   HttpSession session = request.getSession();
	        	   session.setAttribute("user_id", user.getId_user());
	           }
       } 
	      response.getWriter().print(userJson);
	}

}
