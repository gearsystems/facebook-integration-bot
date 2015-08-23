class ComplaintDetails {
String id;
String status;
String name;
String problem;
String location;
String date;
public ComplaintDetails()
{
    
}
public ComplaintDetails(String pehchaan,String paristhiti,String naam,String taklif,String jagah,String taarikh)
{
    this.id=pehchaan;
    this.status=paristhiti;
    this.name=naam;
    this.problem=taklif;
    this.location=jagah;
    this.date=taarikh;
}
}
