package airfree.xprojets.airfree;

/**
 * Created by guillaume on 25/02/16.
 */
public class user_info
{
    boolean exists;
    int id;
    String mail;
    String password;

    user_info(boolean exists_, int id_,String mail_, String password_)
    {
        exists=exists_;
        mail=mail_;
        password = password_;
    }
}