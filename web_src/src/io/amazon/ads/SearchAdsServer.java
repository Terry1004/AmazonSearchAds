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

import org.apache.log4j.Logger;

import io.amazon.ads.Utilities.RedisEngine;
import io.amazon.ads.StaticObjs.Ad;
import io.amazon.ads.Utilities.MysqlEngine;

/**
 * The search ads server that returns all ads upon given query.
 * @see SearchAdsEngine
 */
@WebServlet(name = "SearchAds", urlPatterns = { "/search-ads" })
public class SearchAdsServer extends HttpServlet {
	
	private static final Logger logger = Logger.getLogger(SearchAdsServer.class);
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
		String adsDataPath = application.getInitParameter("adsDataPath");
	    String budgetDataPath = application.getInitParameter("budgetDataPath");
	    String uiTemplatePath = application.getInitParameter("uiTemplatePath");
	    String adTemplatePath = application.getInitParameter("adTemplatePath");
	    String redisHost = application.getInitParameter("redisHost");
	    String dbSourceUrl = application.getInitParameter("dbSourceUrl");
	    String adsTableName = application.getInitParameter("adsTableName");
		String campaignTableName = application.getInitParameter("campaignTableName");
	    RedisEngine redisEngine = RedisEngine.getInstance(redisHost);
	    MysqlEngine mysqlEngine = MysqlEngine.getInstance(dbSourceUrl, adsTableName, campaignTableName);
	    searchAdsEngine = SearchAdsEngine.getInstance(redisEngine, mysqlEngine, adsDataPath, budgetDataPath);
	    initTemplates(uiTemplatePath, adTemplatePath);
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
	
	private void initTemplates(String uiTemplatePath, String adTemplatePath) {
		try {
			byte[] uiData;
			byte[] adData;
			uiData = Files.readAllBytes(Paths.get(uiTemplatePath));
			uiTemplate = new String(uiData, StandardCharsets.UTF_8);
			adData = Files.readAllBytes(Paths.get(adTemplatePath));
			adTemplate = new String(adData, StandardCharsets.UTF_8);
			logger.info("Templates successfully initialized.");
		} catch (IOException e) {
			logger.error("Templates fail to be initialized.", e);
		}
	}

}
