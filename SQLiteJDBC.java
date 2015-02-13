import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SQLiteJDBC {
    public static void main(String[] args) {
	Connection c = null;
	Statement stmt = null;
	try {
	    Class.forName("org.sqlite.JDBC");
	    c = DriverManager.getConnection("jdbc:sqlite:cards.db");
	    c.setAutoCommit(false);
	    System.out.println("Opened database successfully");

	    stmt = c.createStatement();
	    ResultSet rs = stmt.executeQuery("select * from BASE;");
	    while (rs.next()) {
		String name = rs.getString("name");
		ArrayList<String> types = new ArrayList<String>(Arrays.asList(rs.getString("types").split(", ")));
		int cost = rs.getInt("cost");
		String attrs = rs.getString("attributes");
		ArrayList<String> attributes = null;
		if (attrs != null) {
		    attributes = new ArrayList<String>(Arrays.asList(rs.getString("attributes").split(", ")));
		}

		System.out.println(name + '\t' + types + '\t' + cost + '\t' + attributes);
	    }
	    
	    rs.close();
	    stmt.close();
	    c.close();
	} catch (Exception e) {
	    System.err.println(e.getClass().getName() + ": " + e.getMessage());
	    System.exit(1);
	}
	System.out.println("Table create successfully");
    }
}
