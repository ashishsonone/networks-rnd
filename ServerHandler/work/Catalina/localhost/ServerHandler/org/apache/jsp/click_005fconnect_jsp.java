/*
 * Generated by the Jasper component of Apache Tomcat
 * Version: Apache Tomcat/7.0.52 (Ubuntu)
 * Generated at: 2014-10-01 19:39:36 UTC
 * Note: The last modified time of this file was set to
 *       the last modified time of the source file after
 *       generation to assist with modification tracking.
 */
package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class click_005fconnect_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final javax.servlet.jsp.JspFactory _jspxFactory =
          javax.servlet.jsp.JspFactory.getDefaultFactory();

  private static java.util.Map<java.lang.String,java.lang.Long> _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public java.util.Map<java.lang.String,java.lang.Long> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
  }

  public void _jspDestroy() {
  }

  public void _jspService(final javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response)
        throws java.io.IOException, javax.servlet.ServletException {

    final javax.servlet.jsp.PageContext pageContext;
    javax.servlet.http.HttpSession session = null;
    final javax.servlet.ServletContext application;
    final javax.servlet.ServletConfig config;
    javax.servlet.jsp.JspWriter out = null;
    final java.lang.Object page = this;
    javax.servlet.jsp.JspWriter _jspx_out = null;
    javax.servlet.jsp.PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html;charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\n");
      out.write("\n");
      out.write("<html lang=\"en\">\n");
      out.write("  <head>\n");
      out.write("    <meta charset=\"utf-8\">\n");
      out.write("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
      out.write("    <meta name=\"description\" content=\"ServerHandler\">\n");
      out.write("    <meta name=\"IITB\" content=\"IITB Wi-Fi Load Generator\">\n");
      out.write("    <title>ServerHandler</title>\n");
      out.write("\t\n");
      out.write("\t<link type=\"text/css\" rel=\"stylesheet\" href=\"./css/bootstrap.min.css\" />\n");
      out.write("\t<link type=\"text/css\" rel=\"stylesheet\" href=\"./css/bootstrap-responsive.min.css\" />\n");
      out.write("\t<link type=\"text/css\" rel=\"stylesheet\" href=\"./css/font-awesome.css\" />\n");
      out.write("\t<link type=\"text/css\" rel=\"stylesheet\" href=\"./css/font-awesome-ie7.css\" />\n");
      out.write("\t<link type=\"text/css\" rel=\"stylesheet\" href=\"./css/boot-business.css\" />\n");
      out.write("\t\n");
      out.write("\t\n");
      out.write("  </head>\n");
      out.write("  <body>\n");
      out.write("\n");
      out.write("    <header>\n");
      out.write("      \n");
      out.write("      <div class=\"navbar navbar-fixed-top\">\n");
      out.write("        <div class=\"navbar-inner\">\n");
      out.write("          <div class=\"container\">\n");
      out.write("            <a href=\"index.jsp\" class=\"brand brand-bootbus\">ServerHandler</a>\n");
      out.write("\n");
      out.write("            <button type=\"button\" class=\"btn btn-navbar\" data-toggle=\"collapse\" data-target=\".nav-collapse\">\n");
      out.write("              <span class=\"icon-bar\"></span>\n");
      out.write("              <span class=\"icon-bar\"></span>\n");
      out.write("              <span class=\"icon-bar\"></span>\n");
      out.write("            </button>\n");
      out.write("          </div>\n");
      out.write("        </div>\n");
      out.write("      </div>\n");
      out.write("    <br>\n");
      out.write("    <br>     \n");
      out.write("    </header>\n");
      out.write("\n");
      out.write("    <div class=\"content\">\n");
      out.write("      <div class=\"container\">\n");
      out.write("        <div class=\"page-header\">\n");
      out.write("          <h1>Load Generator Test-bed</h1>\n");
      out.write("          <br>\n");
      out.write("          <br>\n");
      out.write("          <h4>Click one by one to run the experiments</h4>\n");
      out.write("          <form action=\"searchresults.jsp\" class=\"form-horizontal form-signin-signup\">\n");
      out.write("            <input type=\"submit\" name=\"startRegistration\" value=\"Start Registration\" class=\"btn btn-primary btn-large\">\n");
      out.write("            <br>\n");
      out.write("            <input type=\"submit\" name=\"stopRegistration\" value=\"Stop Registration\" class=\"btn btn-primary btn-large\">\n");
      out.write("            <br>\n");
      out.write("            <input type=\"text\" name=\"eventsFile\" placeholder=\"Upload Event File\">\n");
      out.write("            <input type=\"submit\" name=\"startExperiment\" value=\"Start Experiment\" class=\"btn btn-primary btn-large\">\n");
      out.write("            <br>\n");
      out.write("            <input type=\"submit\" name=\"stopExperiment\" value=\"Stop Experiment\" class=\"btn btn-primary btn-large\">\n");
      out.write("          </form>\n");
      out.write("          <form action=\"searchresults.jsp\" class=\"form-horizontal form-signin-signup\">\n");
      out.write("            <input type=\"submit\" name=\"startRegistration\" value=\"Start Registration\" class=\"btn btn-primary btn-large\">\n");
      out.write("            <br>\n");
      out.write("            <input type=\"submit\" name=\"stopRegistration\" value=\"Stop Registration\" class=\"btn btn-primary btn-large\">\n");
      out.write("            <br>\n");
      out.write("            <input type=\"text\" name=\"eventsFile\" placeholder=\"Upload Event File\">\n");
      out.write("            <input type=\"submit\" name=\"startExperiment\" value=\"Start Experiment\" class=\"btn btn-primary btn-large\">\n");
      out.write("            <br>\n");
      out.write("            <input type=\"submit\" name=\"stopExperiment\" value=\"Stop Experiment\" class=\"btn btn-primary btn-large\">\n");
      out.write("          </form>\n");
      out.write("          <form action=\"searchresults.jsp\" class=\"form-horizontal form-signin-signup\">\n");
      out.write("            <input type=\"submit\" name=\"startRegistration\" value=\"Start Registration\" class=\"btn btn-primary btn-large\">\n");
      out.write("            <br>\n");
      out.write("            <input type=\"submit\" name=\"stopRegistration\" value=\"Stop Registration\" class=\"btn btn-primary btn-large\">\n");
      out.write("            <br>\n");
      out.write("            <input type=\"text\" name=\"eventsFile\" placeholder=\"Upload Event File\">\n");
      out.write("            <input type=\"submit\" name=\"startExperiment\" value=\"Start Experiment\" class=\"btn btn-primary btn-large\">\n");
      out.write("            <br>\n");
      out.write("            <input type=\"submit\" name=\"stopExperiment\" value=\"Stop Experiment\" class=\"btn btn-primary btn-large\">\n");
      out.write("          </form>\n");
      out.write("          <form action=\"searchresults.jsp\" class=\"form-horizontal form-signin-signup\">\n");
      out.write("            <input type=\"submit\" name=\"startRegistration\" value=\"Start Registration\" class=\"btn btn-primary btn-large\">\n");
      out.write("            <br>\n");
      out.write("            <input type=\"submit\" name=\"stopRegistration\" value=\"Stop Registration\" class=\"btn btn-primary btn-large\">\n");
      out.write("            <br>\n");
      out.write("            <input type=\"text\" name=\"eventsFile\" placeholder=\"Upload Event File\">\n");
      out.write("            <input type=\"submit\" name=\"startExperiment\" value=\"Start Experiment\" class=\"btn btn-primary btn-large\">\n");
      out.write("            <br>\n");
      out.write("            <input type=\"submit\" name=\"stopExperiment\" value=\"Stop Experiment\" class=\"btn btn-primary btn-large\">\n");
      out.write("          </form>\n");
      out.write("        </div>\n");
      out.write("      </div>\n");
      out.write("    </div>\n");
      out.write("\n");
      out.write("    <footer>\n");
      out.write("      <hr class=\"footer-divider\">\n");
      out.write("      <div class=\"container\">\n");
      out.write("        <p>\n");
      out.write("          &copy; 2014-3000 ServerHandler. All Rights Reserved\n");
      out.write("        </p>\n");
      out.write("      </div>\n");
      out.write("    </footer>\n");
      out.write("    <script type=\"text/javascript\" src=\"./js/jquery.min.js\"></script>\n");
      out.write("    <script type=\"text/javascript\" src=\"./js/bootstrap.min.js\"></script>\n");
      out.write("    <script type=\"text/javascript\" src=\"./js/boot-business.js\"></script>\n");
      out.write("  </body>\n");
      out.write("</html>\n");
      out.write("\n");
      out.write("      \n");
    } catch (java.lang.Throwable t) {
      if (!(t instanceof javax.servlet.jsp.SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
