
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

//class ComplaintDetailsDao {
//    public static void main(String[] args)
//    {
//        ComplaintDetailsDao cd = new ComplaintDetailsDao();
//        ComplaintDetails cmpd = new ComplaintDetails("Ghopdu","paani ki kami","australia","2015-08-22");
////        System.setProperty("http.proxyPort","3306");
//        cd.connect();
//        cd.adddetails(cmpd);
//        System.out.println("successfully added");
////        System.out.println(cd.getdetails());
//    }
//}
class ComplaintDetailsDao
{
    Connection con=null;
    public void connect()
    {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/complaints","root","sql");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    void adddetails(ComplaintDetails d)
    {
        try {
            String query="insert into fbdata values (?,?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1,d.id);
            pst.setString(2,d.status);
            pst.setString(3,d.name);
            pst.setString(4,d.problem);
            pst.setString(5,d.location);
            pst.setString(6,d.date);
            pst.executeUpdate();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    boolean isPresent(String Rid)
    {
        String query = "select * from fbdata where id = ?";
        boolean p=false;
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, Rid);
            ResultSet rs=pst.executeQuery();
            System.out.println(rs);
            while(rs.next())
            {
                if(rs.getString("id").equals(Rid))
                {
                    p=true;
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return p;
    }
    ComplaintDetails getdetails()
    {
        ComplaintDetails complaint = new ComplaintDetails();
        try
        {
            String query = "select Name,Problem,Location,Date from fbdata where Name='Ghopdu'" ;
            Statement st = con.createStatement();
            ResultSet rs=st.executeQuery(query);
            rs.next();
            String name = rs.getString("1");
            String problem = rs.getString("2");
            String location = rs.getString("3");
            String date = rs.getString("4");
            complaint.name=name;
            complaint.problem=problem;
            complaint.location=location;
            complaint.date=date;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }   
        return complaint;
    }
}