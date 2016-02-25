package airfree.xprojets.airfree;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Main activity :
 *      Displays the list of items
 *      Allows the user to choose filters, and to search for a special article
 */
public class Recherche extends Activity {
    /*db part (test for the adapter)*/
    private static SQLiteDatabase sqliteDB = null;
    private SimpleCursorAdapterWithClick myAdapter;
    public String[] boutique = new String[] {
        "Vendeur",
        "Vendeur1",
        "Vendeur2",
        "Vendeur3",
        "Vendeur4",
        "Vendeur5",
        "Vendeur6",
        "Vendeur7",
        "Vendeur8",
        "Vendeur9",
            "Vendeur10",
            "Vendeur11",
            "Vendeur12",
            "Vendeur13",
            "Vendeur14",
            "Vendeur15",
            "Vendeur16",
            "Vendeur17",
            "Vendeur18",
            "Vendeur19",
            "Vendeur20",
            "Vendeur21",
            "Vendeur22",
            "Vendeur23",
            "Vendeur24",
            "Vendeur25",
            "Vendeur26",
            "Vendeur27",
            "Vendeur28",
            "Vendeur29",
            "Vendeur30",
            "Vendeur31",
            "Vendeur32",
    };
    private final String[] groupTitle = new String[] {
        "Boutiques",
        "Prix",
        "Note",
        "En stock"
    };
    // end of the db part
    HashMap<String, List<String>> listDataChild;
    private DrawerLayout menuLayout;
    private ExpandableListView menuElementsList;
    private ActionBarDrawerToggle menuToggle;
    private PopupWindow popUpConnection;
    private PopupWindow popUpAccount;
    private PopupWindow popUpArticle;
    private FrameLayout layout_MainMenu;
    private DisplayMetrics metrics;
    private user_info user;

    boolean click = true;
    boolean connected = false;
    LinkedList<String> checked_vendors = new LinkedList<String>();
    double[] prices = new double[] {0, 10000};
    double[] notes = new double[] {0, 5};
    int available = 0;
    String[] keywords = new String[] {};

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        /* load the recherche.xml layout */
        setContentView(R.layout.recherche);
        for(int k = 0; k<boutique.length; k++) {
            checked_vendors.add(boutique[k]);
        }
        /* get the size of the screen in order to place the elements of the ActionBar */
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        /* hide the ActionBar */
        getActionBar().hide();
        getActionBar().setDisplayHomeAsUpEnabled(true);

        /* begin of the creation and treatment of the db */
        create_db();
        /* get the elements from the db */
        final Cursor cursor = filter(checked_vendors, prices, notes, available, keywords);
        /* every article will on click display a popup with its value (here is only the vendor handled */
        SimpleCursorAdapterWithClick.CallBack callBack = new SimpleCursorAdapterWithClick.CallBack() {
                @Override
                public void callback(CharSequence vendeur, CharSequence produit, CharSequence prix, float note, CharSequence description) {
                    /* set the name of the vendor */
                    TextView vendeur2 = (TextView) popUpArticle.getContentView().findViewById(R.id.vendeur);
                    vendeur2.setText(vendeur);
                    /* set the name of the product */
                    TextView product2 = (TextView) popUpArticle.getContentView().findViewById(R.id.intitule);
                    product2.setText(produit);
                    /* set the name of the product */
                    TextView prix2 = (TextView) popUpArticle.getContentView().findViewById(R.id.prix);
                    prix2.setText(prix);
                    /* set the name of the product */
                    RatingBar note2 = (RatingBar) popUpArticle.getContentView().findViewById(R.id.notation);
                    note2.setRating(note);
                    /* set the name of the product */
                    TextView description2 = (TextView) popUpArticle.getContentView().findViewById(R.id.details);
                    description2.setText(description);
                    /* display the popup */
                    layout_MainMenu.getForeground().setAlpha(220);
                    popUpArticle.showAtLocation(findViewById(R.id.mainmenu), Gravity.CENTER, 0, 0);
                    popUpArticle.update(metrics.widthPixels - 100, metrics.heightPixels - 100);
                }
            };
            /* create the popup for the articles */
            handlePopUpArticle();
            /* create the slide menu */
            handleSlideMenu();
            if(connected){
                /* if the user is connected, we display the button for accessing to his account in the ActionBar */
                ifIsConnected();
            }
            else {
                /* if not, we display in the ActionBar the button for the login popup */
                handlePopUpConnection();
            }
            /* displays the list of items, with the behavior on Click */
            myAdapter = new SimpleCursorAdapterWithClick(this, R.layout.article, cursor, new String[]{"boutique", "produit", "description", "note", "prix"},
                    new int[]{R.id.vendeur, R.id.intitule, R.id.details, R.id.notation, R.id.prix}, R.id.apercu, callBack);
            /* end of the db creation and treatment */
            GridView view = (GridView) findViewById(R.id.grille);//go look for the right layout for our response
            view.setAdapter(myAdapter);// will create one grid element for each response from the db

    }

    /**
     * create and the popup for a random article
     */
    private void handlePopUpArticle() {
        popUpArticle = new PopupWindow();
        /* set the graphical aspect of the popup */
        LayoutInflater inflaterArticle = LayoutInflater.from(this);
        View popUpViewArticle = inflaterArticle.inflate(R.layout.pop_up_article, null);
        popUpArticle.setContentView(popUpViewArticle);
        /* set the behaviour of the popup */
        popUpArticle.setOutsideTouchable(true);
        popUpArticle.setTouchable(true);
        popUpArticle.setFocusable(true);
        popUpArticle.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popUpArticle.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popUpArticle.dismiss();
                /* delete the shadow shape when the popup is dismissed */
                layout_MainMenu.getForeground().setAlpha(0);
                click = true;
            }
        });
        /* the behaviour of the addToCart button */
        Button but = (Button) popUpArticle.getContentView().findViewById(R.id.button1);
        but.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String name = ((TextView) popUpArticle.getContentView().findViewById(R.id.intitule)).getText().toString();
                int id = Integer.parseInt(((TextView) popUpArticle.getContentView().findViewById(R.id.id)).getText().toString());
                float price = Float.parseFloat(((TextView) popUpArticle.getContentView().findViewById(R.id.prix)).getText().toString());
                String vendor = ((TextView) popUpArticle.getContentView().findViewById(R.id.vendeur)).getText().toString();
                String image = ((ImageView) popUpArticle.getContentView().findViewById(R.id.apercu)).getBackground().toString();
                System.out.println(name);
                add_to_cart(id, 1, name, price, vendor, image);
                popUpConnection.dismiss();
            }
        });

    }

    private void handlePopUpConnection() {
        popUpConnection = new PopupWindow();
        LayoutInflater inflater = LayoutInflater.from(this);
        View popUpView = inflater.inflate(R.layout.pop_up_login, null);
        popUpConnection.setContentView(popUpView);
        popUpConnection.setOutsideTouchable(true);
        popUpConnection.setTouchable(true);
        popUpConnection.setFocusable(true);
        popUpConnection.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popUpConnection.getContentView().findViewById(R.id.wrong_passwd).setVisibility(View.GONE);
        popUpConnection.getContentView().findViewById(R.id.registered).setVisibility(View.GONE);
        popUpConnection.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popUpConnection.dismiss();
                layout_MainMenu.getForeground().setAlpha(0);
                click = true;
            }
        });
        /* set a transparent foreground */
        layout_MainMenu = (FrameLayout) findViewById(R.id.mainmenu);
        layout_MainMenu.getForeground().setAlpha(0);
        /* the behaviour of the login button */
        Button but = (Button) findViewById(R.id.loginButton);
        but.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (click) {
                    /* draw a dark shadow on the background */
                    layout_MainMenu.getForeground().setAlpha(220);
                    popUpConnection.showAtLocation(findViewById(R.id.mainmenu), Gravity.CENTER, 0, 0);
                    popUpConnection.update(metrics.widthPixels - 100, metrics.heightPixels - 100);
                    click = false;
                } else {
                    popUpConnection.dismiss();
                }
            }
        });
        /* allows to get the connection identifiers for handling the connection */
        ((Button) popUpConnection.getContentView().findViewById(R.id.connect)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleConnection();
            }
        });
        /* allows to get the register identifiers for handling the connection */
        ((Button) popUpConnection.getContentView().findViewById(R.id.register)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegistration();
            }
        });
    }

    /* create the popup for the personal account of the customer */
    private void handlePopUpMyAccount() {
        /* same as previously : create the popup */
        popUpAccount = new PopupWindow();
        LayoutInflater inflater = LayoutInflater.from(this);
        View popUpView = inflater.inflate(R.layout.pop_up_account, null);
        popUpAccount.setContentView(popUpView);
        popUpAccount.setOutsideTouchable(true);
        popUpAccount.setTouchable(true);
        popUpAccount.setFocusable(true);
        popUpAccount.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popUpAccount.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popUpAccount.dismiss();
                layout_MainMenu.getForeground().setAlpha(0);
                click = true;
            }
        });
        /* behaviour on click and dark shape on the background */
        layout_MainMenu = (FrameLayout) findViewById(R.id.mainmenu);
        layout_MainMenu.getForeground().setAlpha(0);
        Button but = (Button) findViewById(R.id.myAccountButton);
        but.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (click) {
                    layout_MainMenu.getForeground().setAlpha(220);
                    //((FrameLayout) findViewById(R.id.action_bar)).getForeground().setAlpha(220);
                    popUpAccount.showAtLocation(findViewById(R.id.mainmenu), Gravity.CENTER, 0, 0);
                    popUpAccount.update(metrics.widthPixels - 100, metrics.heightPixels - 100);
                    click = false;
                } else {
                    //((FrameLayout) findViewById(R.id.action_bar)).getForeground().setAlpha(0);
                    //layout_MainMenu.getForeground().setAlpha(0);
                    popUpAccount.dismiss();
                    //click = true;
                }
            }
        });
        /* create the link with the database */
        Cursor cursor = sqliteDB.rawQuery("SELECT id as _id, produit, image, vendeur, qty, prix, id_produit FROM PANIER;",null);
        /* populate the list of vendors */
        SimpleCursorAdapter panierAdapter = new SimpleCursorAdapter(this, R.layout.article_panier, cursor, new String[]{"vendeur",},
                new int[]{R.id.vendeur_panier});
        /* display the list of items in the customer's basket and allow him to proceed to payment */
        String paypal_email = "root@airfree.fr";
        ListView panier = (ListView) popUpAccount.getContentView().findViewById(R.id.panier);
        LinearLayout validation = (LinearLayout) popUpAccount.getContentView().findViewById(R.id.validation);
        String payment = "<form action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\" target=\"_top\"> <input type=\"hidden\" name=\"cmd\" value=\"_xclick\"> <input type=\"hidden\" name=\"business\" value=\"";
        payment += paypal_email;
        payment += " \"><input type=\"hidden\" name=\"lc\" value=\"US\"><input type=\"hidden\" name=\"item_name\" value=\"AirFree\"><input type=\"hidden\" name=\"amount\" value=\"";
        payment += total_ammount();
        payment += "\"><input type=\"hidden\" name=\"currency_code\" value=\"EUR\"><input type=\"hidden\" name=\"button_subtype\" value=\"services\"><input type=\"hidden\" name=\"no_note\" value=\"0\"><input type=\"hidden\" name=\"tax_rate\" value=\"0.000\"><input type=\"hidden\" name=\"shipping\" value=\"0.00\"><input type=\"hidden\" name=\"bn\" value=\"PP-BuyNowBF:btn_buynowCC_LG.gif:NonHostedGuest\"><input type=\"image\" src=\"https://www.paypalobjects.com/fr_XC/i/btn/btn_buynowCC_LG.gif\" border=\"0\" name=\"submit\" alt=\"PayPal - la solution de paiement en ligne la plus simple et la plus sécurisée !\"><img alt=\"\" border=\"0\" src=\"https://www.paypalobjects.com/fr_XC/i/scr/pixel.gif\" width=\"1\" height=\"1\"></form>";
        WebView webview = new WebView(this);
        validation.addView(webview);
        webview.loadData(payment, "text/html", null);
        System.out.println(panier.getBackground());
        System.out.println(panierAdapter);
        panier.setAdapter(panierAdapter);
    }

    /* handle the graphic behaviour if the user is connected */
    private void ifIsConnected() {
        findViewById(R.id.loginButton).setVisibility(View.GONE);
        findViewById(R.id.myAccountButton).setVisibility(View.VISIBLE);
        handlePopUpMyAccount();
        /* allow the user to vote for an article */
        popUpArticle.getContentView().findViewById(R.id.noter).setVisibility(View.VISIBLE);
    }

    /* handle the connection of a customer */
    private void handleConnection() {
        /* get his references */
        String email = ((TextView) popUpConnection.getContentView().findViewById(R.id.mail_connect)).getText().toString();
        String mdp = ((TextView) popUpConnection.getContentView().findViewById(R.id.mdp_connect)).getText().toString();
        System.out.println(email);
        user = check_user(email, mdp);
        if(user.exists) {
            /* adapt appearence */
            connected = true;
            ifIsConnected();
            System.out.println("connexion réussie");
            popUpConnection.dismiss();
        }
        else {
            ((TextView) popUpConnection.getContentView().findViewById(R.id.wrong_passwd)).setVisibility(View.VISIBLE);
        }
    }

    /* handle the connection of a customer */
    private void handleRegistration() {
        /* get his references */
        String email = ((TextView) popUpConnection.getContentView().findViewById(R.id.email_register)).getText().toString();
        String mdp = ((TextView) popUpConnection.getContentView().findViewById(R.id.mdp_register)).getText().toString();
        System.out.println(email);
        add_user(email, mdp);
        ((TextView) popUpConnection.getContentView().findViewById(R.id.registered)).setVisibility(View.VISIBLE);
    }

    /* handle the appearance of the slide menu */
    private void handleSlideMenu() {
        /* populate the elements of the menu */
        prepareListData();
        menuElementsList = (ExpandableListView) findViewById(R.id.menu_elements);
        menuElementsList.setGroupIndicator(getResources().getDrawable(R.drawable.group_indicator));
        menuElementsList.setMinimumWidth(metrics.widthPixels / 2);
        ExpandableListAdapter myAdapter = new ExpandableListAdapter(this, groupTitle, listDataChild,
                R.layout.menu, R.layout.prix, R.layout.note,R.layout.menu, R.layout.menu_item, R.id.intitule, R.id.intitule_prix, R.id.intitule_note, R.id.intitule, R.id.menu_element_title);
        menuLayout = (DrawerLayout) findViewById(R.id.menu_layout);
        menuElementsList.setAdapter(myAdapter);
        menuElementsList.setOnItemClickListener(new DrawerItemClickListener());

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        menuToggle = new ActionBarDrawerToggle(this, /* host Activity */
                menuLayout, /* DrawerLayout object */
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                //invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                findViewById(R.id.search).setVisibility(View.GONE);
                findViewById(R.id.title_text).setVisibility(View.VISIBLE);
                keywords = ((SearchView) findViewById(R.id.search)).getQuery().toString().split(" ");
                refreshSearch();
            }

            public void onDrawerOpened(View drawerView) {
                //invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                findViewById(R.id.search).setVisibility(View.VISIBLE);
                findViewById(R.id.title_text).setVisibility(View.GONE);
            }
        };
        /* handle the actions on the sliding menu */
        menuLayout.setDrawerListener(menuToggle);
        /* display the menu on click on the search icon */
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setVisibility(View.VISIBLE);
        imageButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (menuLayout.isDrawerOpen(menuElementsList)) {
                    menuLayout.closeDrawer(menuElementsList);
                    findViewById(R.id.search).setVisibility(View.GONE);
                    findViewById(R.id.title_text).setVisibility(View.VISIBLE);
                } else {
                    menuLayout.openDrawer(menuElementsList);
                    findViewById(R.id.search).setVisibility(View.VISIBLE);
                    findViewById(R.id.title_text).setVisibility(View.GONE);
                }
            }
        });
    }

    public void refreshSearch() {
        keywords = ((SearchView) findViewById(R.id.search)).getQuery().toString().split(" ");
        System.out.println("vendors"+checked_vendors);
        System.out.println("prices"+prices);
        System.out.println("notes"+notes);
        System.out.println("stock"+available);
        System.out.println("keywords"+keywords);
        final Cursor cursor = filter(checked_vendors, prices, notes, available, keywords);
        /* every article will on click display a popup with its value (here is only the vendor handled */
        SimpleCursorAdapterWithClick.CallBack callBack = new SimpleCursorAdapterWithClick.CallBack() {
            @Override
            public void callback(CharSequence vendeur, CharSequence produit, CharSequence prix, float note, CharSequence description) {
                    /* set the name of the vendor */
                TextView vendeur2 = (TextView) popUpArticle.getContentView().findViewById(R.id.vendeur);
                vendeur2.setText(vendeur);
                    /* set the name of the product */
                TextView product2 = (TextView) popUpArticle.getContentView().findViewById(R.id.intitule);
                product2.setText(produit);
                    /* set the name of the product */
                TextView prix2 = (TextView) popUpArticle.getContentView().findViewById(R.id.prix);
                prix2.setText(prix);
                    /* set the name of the product */
                RatingBar note2 = (RatingBar) popUpArticle.getContentView().findViewById(R.id.notation);
                note2.setRating(note);
                    /* set the name of the product */
                TextView description2 = (TextView) popUpArticle.getContentView().findViewById(R.id.details);
                description2.setText(description);
                    /* display the popup */
                layout_MainMenu.getForeground().setAlpha(220);
                popUpArticle.showAtLocation(findViewById(R.id.mainmenu), Gravity.CENTER, 0, 0);
                popUpArticle.update(metrics.widthPixels - 100, metrics.heightPixels - 100);
            }
        };
            /* create the popup for the articles */
        handlePopUpArticle();
            /* create the slide menu */
        handleSlideMenu();
        if(connected){
                /* if the user is connected, we display the button for accessing to his account in the ActionBar */
            ifIsConnected();
        }
        else {
                /* if not, we display in the ActionBar the button for the login popup */
            handlePopUpConnection();
        }
            /* displays the list of items, with the behavior on Click */
        myAdapter = new SimpleCursorAdapterWithClick(this, R.layout.article, cursor, new String[]{"boutique", "produit", "description", "note", "prix"},
                new int[]{R.id.vendeur, R.id.intitule, R.id.details, R.id.notation, R.id.prix}, R.id.apercu, callBack);
            /* end of the db creation and treatment */
        GridView view = (GridView) findViewById(R.id.grille);//go look for the right layout for our response
        view.setAdapter(myAdapter);// will create one grid element for each response from the db
    }
    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        List<String> vendors = new ArrayList<String>();
        for(int k=0; k<boutique.length;k++) {
            vendors.add(boutique[k]);
        };
        listDataChild.put(groupTitle[0], vendors); // Header, Child data

        List<String> prices = new ArrayList<String>();
        prices.add("Prix");
        listDataChild.put(groupTitle[1], prices);

        List<String> notes = new ArrayList<String>();
        notes.add("Note");
        listDataChild.put(groupTitle[2], notes);

        List<String> stock = new ArrayList<String>();
        stock.add("En stock");
        listDataChild.put(groupTitle[3], stock);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Hide menu element when menu is opened
        boolean drawerOpen = menuLayout.isDrawerOpen(menuElementsList);
        //menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        //System.out.println("bouton appuyé");
        if (menuToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position,
                                long id) {
            /* action you want to do when an item is selected */
            //System.out.println("ok");
            //System.out.println(id);
        }
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        menuToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        menuToggle.onConfigurationChanged(newConfig);
    }

    protected Drawable drawableFromUrl(URL url) throws IOException {
        Drawable ret = null;
        InputStream stream = null;
        try {
            stream = url.openStream();
            ret = Drawable.createFromStream(stream, "src");
        } catch (IOException ioe) {
            throw ioe;
        } catch (Exception e) {
            Log.e("drawableFromUrl", "Failed to read image from URL !", e);
            throw new IOException("ImageFromUrlFailure",e);
        } finally {
            if (stream != null) try { stream.close(); } catch (Exception ex) { Log.wtf("drawableFromUrl", "Failed to gracefully close the stream !", ex); }
        }
        return ret;
    }

    protected Drawable drawableFromUrl(String urls) throws IOException {
        URL url = new URL(urls);
        return drawableFromUrl(url);
    }

    public Cursor filter (LinkedList<String> boutiques, double[] prix, double[] note, int EnStock,String[] keywords)
    {
        String FILTRE_BOUTIQUES = "(";
        for (int i=0;i<boutiques.size();i++)
        {
            FILTRE_BOUTIQUES += "boutique = \""+ boutiques.get(i)+"\"";
            if (i<boutiques.size()-1) FILTRE_BOUTIQUES += " OR ";
            else FILTRE_BOUTIQUES += ")";
        }
        String FILTRE_PRIX = "prix >=" + prix[0] + " AND prix <= "+ prix [1] ;
        String FILTRE_NOTE = "note >= " + note[0] + " AND note <= "+ note[1];
        String FILTRE_ENSTOCK = "en_stock >= " + EnStock;
        String FILTRE_RESEARCH_TEXT;
        if(keywords.length != 0) {
            FILTRE_RESEARCH_TEXT = "(";
            for (int i = 0; i < keywords.length; i++) {
                FILTRE_RESEARCH_TEXT += "description LIKE '\"%" + keywords[i] + "%\"'";
                if (i < keywords.length - 1) FILTRE_RESEARCH_TEXT += " AND ";
            }
            FILTRE_RESEARCH_TEXT += ")";
        }
        else {
            FILTRE_RESEARCH_TEXT = "1 = 1";
        }
        System.out.println("SELECT id as _id, produit, prix, note, boutique, description, image FROM INVENTAIRE WHERE " + FILTRE_PRIX + " AND " + FILTRE_NOTE + " AND " + FILTRE_ENSTOCK + " AND " + FILTRE_BOUTIQUES +
                " AND " + FILTRE_RESEARCH_TEXT);
        return   sqliteDB.rawQuery("SELECT id as _id, produit, prix, note, boutique, description, image FROM INVENTAIRE WHERE " + FILTRE_PRIX + " AND " + FILTRE_NOTE + " AND " + FILTRE_ENSTOCK + " AND " + FILTRE_BOUTIQUES +
                " AND " + FILTRE_RESEARCH_TEXT,null);

    }


    public SQLiteDatabase create_db ()
    {
        sqliteDB = null;
        sqliteDB = this.openOrCreateDatabase("Android", MODE_PRIVATE, null);
        sqliteDB.execSQL("DROP TABLE INVENTAIRE;");
        sqliteDB.execSQL("CREATE TABLE IF NOT EXISTS INVENTAIRE (id INT PRIMARY KEY, produit CHAR , boutique CHAR(30) NOT NULL, prix REAL NOT NULL,note REAL,en_stock INT NOT NULL, description TEXT,image TEXT);");
        // Remplir la table ??
        String produit;
        String boutique;
        String[] descriptions = {"Adidas Originals Superstar, Chaussons Sneaker Mixte Enfant",
                "Wall Street. 2005. Profitant de l'aveuglement gÃ©nÃ©ralisÃ© des grosses banques, des medias et du gouvernement, quatre outsiders anticipent l?explosion de la bulle financiÃ¨re et mettent au point? le casse du siÃ¨cle ! Michael Burry, Mark Baum, Jared Vennett et Ben Rickert : des personnages visionnaires et hors du commun qui vont parier contre les banques... et tenter de rafler la mise "
                ,"100 NEW cards (80 White cards and 20 Black cards),12 bonus blank cards (8 blank White cards and 4 blank Black cards),Professionally printed on premium playing cards (100% compatible with Cards Against Humanity)",
                "8 GB of internal storage. Free unlimited cloud storage for all Amazon content and photos taken with Fire devices. Add a microSD card for up to 128 GB of additional storage",
                "ased on an original new story by J.K. Rowling, Jack Thorne and John Tiffany, a new play by Jack Thorne, Harry Potter and the Cursed Child is the eighth story in the Harry Potter series and the first official Harry Potter story to be presented on stage. The play will receive its world premiere in Londonâ€™s West End on July 30, 2016. " };
        Random randomGenerator = new Random();
        int nb_boutiques = 5;
        for (int i = 0; i < 10; i++) {
            produit = "produit "+i;
            boutique = "Vendeur"+ randomGenerator.nextInt(nb_boutiques);
            double prix = randomGenerator.nextDouble()*prices[1];
            double note = randomGenerator.nextDouble()*notes[1];
            int en_stock = 10;
            String description = descriptions[randomGenerator.nextInt(descriptions.length)];
            String image = "t";
            sqliteDB.execSQL("INSERT INTO INVENTAIRE (produit, boutique, prix, note, en_stock, description, image) VALUES(\"" + produit + "\",\"" + boutique + "\"," + prix + "," + note + "," + en_stock + ",\"" + description + "\",\"" + image + "\");");
        }

        sqliteDB.execSQL("DROP TABLE USERS;");
        sqliteDB.execSQL("CREATE TABLE IF NOT EXISTS USERS (id INTEGER PRIMARY KEY, mail VARCHAR(30) NOT NULL,password VARCHAR(30));");
        sqliteDB.execSQL("INSERT INTO USERS (mail, password) VALUES(\"root@airfree.fr\", \"root\")");
        sqliteDB.execSQL("DROP TABLE PANIER;");
        sqliteDB.execSQL("CREATE TABLE IF NOT EXISTS PANIER (id INTEGER PRIMARY KEY, id_produit INT ,qty INT,produit VARCHAR(30) NOT NULL,prix REAL,vendeur VARCHAR(30), image TEXT);");
        return sqliteDB;
    }

    public void add_user (String mail_,String password_ )
    {
        sqliteDB.execSQL("INSERT INTO USERS (mail, password) VALUES(\""+mail_+"\",\""+password_+"\")");
    }


    public user_info check_user (String mail_,String password_)
    {
        int id=-1;
        boolean exists;
        Cursor result = sqliteDB.rawQuery("SELECT * FROM USERS WHERE mail=\"" + mail_ + "\" AND " + "password=\"" + password_ + "\";",null);

        if (result.moveToFirst())
        {
            exists=true;
            id=result.getInt(0);
        }
        else  exists = false;

        user_info user=new user_info(exists,id,mail_,password_);

        return user;
    }

    public void add_to_cart (int id,int qty, String produit, float prix, String vendeur,String image)
    {
        Cursor result = sqliteDB.rawQuery("SELECT * FROM PANIER WHERE id="+id+";",null);

        if (result.moveToFirst())
        {
            int c_qty = result.getInt(1)+qty;
            sqliteDB.execSQL("UPDATE  PANIER SET qty =" +c_qty+ ";");

        }
        else
            sqliteDB.execSQL("INSERT INTO PANIER (id_produit, qty, produit, prix, vendeur, image) VALUES("+id+","+qty+", \""+produit+"\", \""+ prix+"\",\""+vendeur+"\",\""+ image +"\");");
    }


    public void delete_from_cart  (int id)
    {
        sqliteDB.execSQL("DELETE FROM PANIER WHERE id = " + id + ";");
    }

    public double total_ammount ()
    {
        Cursor cur = sqliteDB.rawQuery("SELECT SUM(prix*qty) FROM PANIER", null);
        if(cur.moveToFirst())
        {
            return cur.getDouble(0);
        }
        return 0;
    }
}

