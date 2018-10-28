package io.amazon.ads;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.amazon.ads.Utilities.RedisEngine;
import io.amazon.ads.Utilities.MysqlEngine;

/**
 * Servlet implementation class SearchAdsServer
 */
@WebServlet(name = "SearchAds", urlPatterns = { "/search-ads" })
public class SearchAdsServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static SearchAdsEngine searchAdsEngine;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchAdsServer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		ServletContext application = config.getServletContext();
		String adsDataFilePath = application.getInitParameter("adsDataFilePath");
	    String budgetDataFilePath = application.getInitParameter("budgetDataFilePath");
	    String uiTemplateFilePath = application.getInitParameter("uiTemplateFilePath");
	    String adsTemplateFilePath = application.getInitParameter("adsTemplateFilePath");
	    String redisHost = application.getInitParameter("redisHost");
	    String mysqlHost = application.getInitParameter("mysqlHost");
	    String mysqlDB = application.getInitParameter("mysqlDB");
	    String mysqlUser = application.getInitParameter("mysqlUser");
	    String mysqlPassword = application.getInitParameter("mysqlPassword");
	    RedisEngine redisEngine = RedisEngine.getInstance(redisHost);
	    MysqlEngine mysqlEngine = MysqlEngine.getInstance(mysqlHost, mysqlDB, mysqlUser, mysqlPassword);
	    searchAdsEngine = SearchAdsEngine.getInstance(redisEngine, mysqlEngine, adsDataFilePath, budgetDataFilePath);
	    searchAdsEngine.init();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doHead(HttpServletRequest, HttpServletResponse)
	 */
	protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
