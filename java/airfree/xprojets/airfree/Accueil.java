package airfree.xprojets.airfree;

import android.os.Bundle;
import android.widget.Button;
import android.view.*;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.app.*;



public class Accueil extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.actionbar, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        final Button loginButton = (Button) findViewById(R.id.bouton_debut);
        loginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Accueil.this, Recherche.class);
                startActivity(intent);
            }
        });
    }
}
