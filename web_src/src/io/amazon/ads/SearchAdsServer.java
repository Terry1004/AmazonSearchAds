package io.amazon.ads;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.amazon.ads.Utilities.RedisEngine;
import io.amazon.ads.StaticObjs.Ad;
import io.amazon.ads.Utilities.MysqlEngine;

/**
 * The search ads server that returns all ads upon given query.
 * @see SearchAdsEngine
 */
@WebServlet(name = "SearchAds", urlPatterns = { "/search-ads" })
public class SearchAdsServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static SearchAdsEngine searchAdsEngine;
    private String uiTemplate = "";
    private String adTemplate = "";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchAdsServer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * Called upon initialization of the search ads server. Initialize a core 
	 * <code>SearchAdsEngine</code> object for handling incoming queries.
	 * @param config A ServletConfig instance.
	 * @throws ServletException A ServletException instance. Thrown by the parent class.
	 * @see Servlet#init(ServletConfig)
	 * @see ServletException
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		ServletContext application = config.getServletContext();
		String adsDataFilePath = application.getInitParameter("adsDataFilePath");
	    String budgetDataFilePath = application.getInitParameter("budgetDataFilePath");
	    String uiTemplateFilePath = application.getInitParameter("uiTemplateFilePath");
	    String adTemplateFilePath = application.getInitParameter("adTemplateFilePath");
	    String redisHost = application.getInitParameter("redisHost");
	    String mysqlHost = application.getInitParameter("mysqlHost");
	    String mysqlDB = application.getInitParameter("mysqlDB");
	    String mysqlUser = application.getInitParameter("mysqlUser");
	    String mysqlPassword = application.getInitParameter("mysqlPassword");
	    RedisEngine redisEngine = RedisEngine.getInstance(redisHost);
	    MysqlEngine mysqlEngine = MysqlEngine.getInstance(mysqlHost, mysqlDB, mysqlUser, mysqlPassword);
	    searchAdsEngine = SearchAdsEngine.getInstance(redisEngine, mysqlEngine, adsDataFilePath, budgetDataFilePath);
	    if (searchAdsEngine.init()) {
	    	System.out.println("searchAdsEngine initialized");
	    	try {
				byte[] uiData;
				byte[] adData;
				uiData = Files.readAllBytes(Paths.get(uiTemplateFilePath));
				uiTemplate = new String(uiData, StandardCharsets.UTF_8);
				adData = Files.readAllBytes(Paths.get(adTemplateFilePath));
				adTemplate = new String(adData, StandardCharsets.UTF_8);
				System.out.println("Templates initilized");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Templates fail to be initialized");
			}
	    } else {
	    	System.out.println("searchAdsEngine fails to be initialized");
	    }
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String query = request.getParameter("q");
		List<Ad> ads = searchAdsEngine.selectAds(query);
		String result = uiTemplate;
		String content = "";
		for (Ad ad : ads) {
			String adContent = adTemplate;
			adContent = adContent.replace("$title$", ad.title);
			content = content + adContent;
		}
		result = result.replace("$list$", content);
		response.setContentType("text/html; charset=UTF-8");
		response.getWriter().write(result);	
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
