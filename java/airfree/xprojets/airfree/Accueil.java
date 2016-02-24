package airfree.xprojets.airfree;

import android.os.Bundle;
import android.widget.Button;
import android.view.*;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.app.*;


/**
 * Welcoming activity : only used for redirecting the user according to his language
 */
public class Accueil extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* load the "activity_accueil.xml" layout */
        setContentView(R.layout.activity_accueil);

        /* delete the ActionBar */
        ActionBar mActionBar = getActionBar();
        mActionBar.hide();

        /* behaviour of the buttons on this activity  */
        final Button loginButton = (Button) findViewById(R.id.bouton_francais);
        loginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Accueil.this, Recherche.class);
                startActivity(intent);
            }
        });
        final Button englishButton = (Button) findViewById(R.id.bouton_english);
        englishButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(Welcome.this, Search.class);
                startActivity(intent);*/
            }
        });
        final Button arabianButton = (Button) findViewById(R.id.button_arbian);
        arabianButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(Welcome.this, Search.class);
                startActivity(intent);*/
            }
        });
    }
}
