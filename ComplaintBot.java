import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.exception.FacebookException;
import com.restfb.exception.FacebookGraphException;
import com.restfb.exception.FacebookJsonMappingException;
import com.restfb.exception.FacebookNetworkException;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.exception.FacebookResponseStatusException;
import com.restfb.types.Comment;
import com.restfb.types.Post.Comments;
import com.restfb.types.FacebookType;
import com.restfb.types.Post;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.io.*;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComplaintBot {
   
   private final String appId="608271979275496",appSecret="4b4274fa091a79b20f10a1a2993d26c5";
   private String[] posrep={"Thank you for registering :)","Your complaint has been succesfully registered","Your complaint was recorded!","Complaint registered!! , Happy to help u :D"};
   private String[] negrep={"Invalid format","Sorry :( there seems to be some problem , please check that you have registered a valid complaint","Could not register , please check the format","Please follow the specified format in description."};
   private FacebookClient fbclient;
   private int  lastcnt=0,currcnt;
   private Thread t1,t2;
    public void setProxy(String host, String port)
    {
        System.setProperty("https.proxyHost",host);
        System.setProperty("https.proxyPort",port);
    }
    public void setProxy()
    {
        System.setProperty("https.proxyHost",null);
        System.setProperty("https.proxyPort","3128");
    }
    public void connect()
    {
//        String tokenUrl = "https://graph.facebook.com/oauth/access_token?client_id=" + appId + "&client_secret=" + appSecret
//                + "&grant_type=client_credentials";
//        try
//        {
//        URL url = new URL(tokenUrl);
//        URLConnection yc = url.openConnection();
//        BufferedReader in = new BufferedReader(new InputStreamReader(
//                                    yc.getInputStream()));
//        String inputLine;
//        inputLine=in.readLine();
//        in.close();
//        String accessToken=inputLine.split("=")[1];
//        System.out.println(accessToken);
//        fbclient = new DefaultFacebookClient(accessToken,appSecret);  
//        }
//        catch(IOException e)
//        {
//            e.printStackTrace();
//        }
        String accessToken="CAACEdEose0cBAPWZBB3NxQraupzqUG4JnZBf6YQwyQIOxByoRj2WLdZBVgR77nlto8TZAvBAZCAz98K0rSSgH7H8U49VdGkvkDcTR9mGZC27hY9MRsTieIMVnj1mucnZA9LVyOQZBzZC4gjk9ctymNJuPT5oDwuWv0JZCsuJDWHDg9P1HFoUQBRbu2Ji5pEXSxPTVF9mGFZBJNkpQZDZD";
        fbclient = new DefaultFacebookClient(accessToken);
    }
    public void process()
    {
        
        t1=new Thread(new CommentRead());
        t2=new Thread(new PostRead());
        t2.start();
        t1.start();
    }
    public void respond(String id,String msg)
    {
        try
        {
            try {
            Thread.sleep(1000);
       } catch (InterruptedException ex) {
          ex.printStackTrace();
       }
        FacebookType publishMessageResponse =
        fbclient.publish("/"+id+"/comments", FacebookType.class,
        Parameter.with("message", msg));

        System.out.println("Published message ID: " + publishMessageResponse.getId());
        }
        catch(FacebookException ex)
        {
            ex.printStackTrace();
        }
    }
    public void reply(String id,boolean flag)
    {
        if(flag)
        {
            respond(id,choose(posrep));
        }
        else
            respond(id,choose(negrep));
    }
    boolean isValid(Comment comment)
    {
        String msg=comment.getMessage();
        return msg.split(";").length==2;
    }
    boolean isRegistered(String id)
    {
        ComplaintDetailsDao cd=new ComplaintDetailsDao();
        cd.connect();
        return cd.isPresent(id);
    }
    String choose(String[] arr)
    {
        int x=(int)(Math.random()*4);
        return arr[x];
    }
    class CommentRead implements Runnable
    {
        @Override
        public void run()
        {
            while(true)
        {
            try
            {
                Connection<Post> myFeed = fbclient.fetchConnection("679746972157851/feed", Post.class);
                List<Post> feeds=myFeed.getData();
                int N=feeds.size();
                System.out.println("no.of posts:"+N);
                for(int curr=0 ; curr<N ;curr++)
                    {
                        String msg=feeds.get(curr).getMessage();
//                        System.out.println(msg);
                        if(msg==null)
                            continue; 
                        else
                        {
                           Post currpost=feeds.get(curr);
                           Comments cmnt=currpost.getComments();
                           List<Comment> cmntlist=null;
                           if(cmnt!=null)
                           {
                            cmntlist=cmnt.getData();
                            if(cmntlist!=null)
                            for(Comment comment:cmntlist)
                            {
                                System.out.println("comments="+comment.getMessage()+isRegistered(currpost.getId())+isValid(comment));
                              if(!comment.getFrom().getName().equals("Nitw Complaint Registration") && isValid(comment) && !isRegistered(currpost.getId()))
                              {
                                  ComplaintDetailsDao cd=new ComplaintDetailsDao();
                                  String res[]=comment.getMessage().split(";");
                                  cd.connect();
                                  cd.adddetails(new ComplaintDetails(currpost.getId(),"1",comment.getFrom().getName(),res[0],res[1],comment.getCreatedTime().toString()));
                                  System.out.println("Valid Comment....details added");
                                  respond(comment.getId(),"Your complaint has been registered successfully :)");
                              }
                            }
                           }
                        }
                    }    
            }         
            catch (FacebookJsonMappingException e) {
            } 
            catch (FacebookNetworkException e) {
                System.out.println("API returned HTTP status code " + e.getHttpStatusCode());
                e.printStackTrace();
            }
            catch (FacebookOAuthException e) {  
            } 
            catch (FacebookGraphException e) {
                System.out.println("Call failed. API says: " + e.getErrorMessage());
            } 
            catch (FacebookResponseStatusException e) {
                if (e.getErrorCode() == 200)
                    System.out.println("Permission denied!");
            } 
            catch (FacebookException e) {
            }
            
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
        }
        }
    }
    class PostRead implements Runnable
    {
        @Override
        public void run()
        {
            while(true)
        {
            try
            {
                Connection<Post> myFeed = fbclient.fetchConnection("679746972157851/feed", Post.class);
                List<Post> feeds=myFeed.getData();
                currcnt=feeds.size();
                if(currcnt>lastcnt)
                {
                    //System.out.println("no.of posts:"+currcnt);
                    for(int curr=0 ; curr<currcnt-lastcnt ;curr++)
                    {
                        String msg=feeds.get(curr).getMessage();
                        System.out.println(msg);
                        if(msg==null || feeds.get(curr).getFrom().getName().equals("Nitw Complaint Registraion"))
                            continue;
                        
                        String[] res = msg.split(";");
                        
                        for(String s : res)
                        {
                            System.out.println(s);
                        }
                        if(res.length!=2)
                        {
                            //respond illegal format
                            reply(feeds.get(curr).getId(),false);
                        }
                        else
                        {
                           System.out.println(feeds.get(curr));
                            ComplaintDetails cds=new ComplaintDetails(feeds.get(curr).getId(),"1",feeds.get(curr).getFrom().getName(),res[0],res[1],feeds.get(curr).getCreatedTime().toString());
                            ComplaintDetailsDao cd=new ComplaintDetailsDao();
                            cd.connect();
                            cd.adddetails(cds);
                            System.out.println("successfully added");
                            reply(feeds.get(curr).getId(),true);
                        }
                    }
                    
                    lastcnt=currcnt;
                }
                else
                    lastcnt=currcnt;
            }         
            catch (FacebookJsonMappingException e) {
            } 
            catch (FacebookNetworkException e) {
                System.out.println("API returned HTTP status code " + e.getHttpStatusCode());
                e.printStackTrace();
            }
            catch (FacebookOAuthException e) {  
            } 
            catch (FacebookGraphException e) {
                System.out.println("Call failed. API says: " + e.getErrorMessage());
            } 
            catch (FacebookResponseStatusException e) {
                if (e.getErrorCode() == 200)
                    System.out.println("Permission denied!");
            } 
            catch (FacebookException e) {
            }
            
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
        }
    }
    
    }
    public static void main(String[] args)
    {
        ComplaintBot fb = new ComplaintBot(); 
        fb.setProxy("172.30.0.12","3128");
        fb.connect();
        fb.process();
    }
}