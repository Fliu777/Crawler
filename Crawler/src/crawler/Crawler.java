/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package crawler;

/**
 *
 * @author Frank
 */
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
 
 
public class Crawler {
	public static DB db = new DB();
 
	public static void main(String[] args) throws SQLException, IOException {
		db.runSql2("TRUNCATE Record;");
		processPage("http://www.kelk.com",0);
	}
 
	public static void processPage(String URL, int layer) throws SQLException, IOException{
               /* if (layer==5){
                    return;
                }*/
            
		//check if the given URL is already in database
                int hacklimit=50;
                String sql;
                if (URL.length()>hacklimit){
                    sql="select * from Record where URL = '"+URL.substring(0,hacklimit)+"'";
                }
                else{
                    sql="select * from Record where URL = '"+URL+"'";                    
                }
		ResultSet rs = db.runSql(sql);
		if(rs.next()){
 
		}else{
                    
                        System.out.println(URL+" depth:"+layer);
			//store the URL to database to avoid parsing again
			sql = "INSERT INTO  `Crawler`.`Record` " + "(`URL`) VALUES " + "(?);";
			PreparedStatement stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                        
                        if (URL.length()>hacklimit) stmt.setString(1, URL.substring(0,hacklimit));
                        else stmt.setString(1, URL);
			stmt.execute();
 
			//get useful information
                        org.jsoup.Connection connection = Jsoup.connect(URL);
                        connection.timeout(0); // timeout in millis
                        
			Document doc = connection.ignoreContentType(true).get();
                        //System.out.println(doc.text());

			/*if(doc.text().contains("research")){
				System.out.println(URL);
			}*/
 
			//get all links and recursively call the processPage method
			Elements questions = doc.select("a[href]");
			for(Element link: questions){
				//if(link.attr("href").contains("mit.edu"))
                                if (false==link.attr("abs:href").startsWith("http")) {
                                } 
                                else if ( link.attr("abs:href").indexOf("kelk.com")==-1  ) {
                                } 
                                //else if (link.attr("abs:href").indexOf("#")!=-1)
                                //    processPage(link.attr("abs:href").substring(0,  link.attr("abs:href").indexOf("#")) , layer+1 );
                                else
                                    processPage(link.attr("abs:href"), layer+1) ;
			}
		}
	}
}