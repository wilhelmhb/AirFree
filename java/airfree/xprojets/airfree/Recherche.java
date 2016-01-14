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
import android.widget.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Recherche extends Activity {
    // part db (test for the adapter)
    private List bookTitles;
    private final String dbName = "Android";
    private static SQLiteDatabase sqliteDB = null;
    private SimpleCursorAdapter myAdapter;
    private final String tableName = "BestSellers";
    private final String[] bookTitle = new String[] {
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
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur",
        "Vendeur"
    };
    private final String[] groupTitle = new String[] {
        "Boutiques",
        "Prix",
        "Note",
        "En stock",
        "Autres",
        "Autres"
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

    boolean click = true;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        System.out.println("activity launched");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recherche);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        getActionBar().hide();
        getActionBar().setDisplayHomeAsUpEnabled(true);

        /* begin of the creation and treatment of the db */
        sqliteDB = null;
        try {
            sqliteDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            //sqliteDB.execSQL("DROP TABLE " + tableName +";");
            sqliteDB.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " (id INTEGER PRIMARY KEY, bookTitle VARCHAR);");
            int i = 0;
            for (String ver : bookTitle) {
                sqliteDB.execSQL("INSERT INTO " + tableName + " Values ("+i+",'" + ver + "');");
                i++;
            }
            final Cursor cursor = sqliteDB.rawQuery("SELECT id as _id, bookTitle FROM " + tableName, null);
            SimpleCursorAdapterWithClick.CallBack callBack = new SimpleCursorAdapterWithClick.CallBack() {
                @Override
                public void callback(CharSequence vendeur) {
                    System.out.println("button pressed");
                    /*ImageView apercu = (ImageView) popUpArticle.getContentView().findViewById(R.id.apercu);
                    apercu.setImageResource(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));*/
                    TextView vendeur2 = (TextView) popUpArticle.getContentView().findViewById(R.id.vendeur);
                    vendeur2.setText(vendeur);
                    layout_MainMenu.getForeground().setAlpha(220);
                    popUpArticle.showAtLocation(findViewById(R.id.mainmenu), Gravity.CENTER, 0, 0);
                    popUpArticle.update(metrics.widthPixels - 100, metrics.heightPixels - 100);
                }
            };
            handlePopUpArticle();
            handleSlideMenu();
            if(connected){
                ifIsConnected();
            }
            else {
                handlePopUpConnection();
            }
            myAdapter = new SimpleCursorAdapterWithClick(this, R.layout.article, cursor, new String[]{"bookTitle",},
                    new int[]{R.id.vendeur}, R.id.apercu, callBack);
            /* end of the db creation and treatment */
            GridView view = (GridView) findViewById(R.id.grille);//go look for the right layout for our response
            view.setAdapter(myAdapter);// will create one grid element for each response from the db
        }
        /* exceptions treatement (db) */
        catch(SQLiteException e) {System.out.println("pas cool");}
        finally {
            if (sqliteDB != null) {
                sqliteDB.execSQL("DELETE FROM " + tableName);
                sqliteDB.close();
            }
        }
        /* end of exceptions treatement */
        System.out.println("end of launching");
    }


    private void handlePopUpArticle() {
        popUpArticle = new PopupWindow();
        LayoutInflater inflaterArticle = LayoutInflater.from(this);
        View popUpViewArticle = inflaterArticle.inflate(R.layout.pop_up_article, null);
        popUpArticle.setContentView(popUpViewArticle);
        popUpArticle.setOutsideTouchable(true);
        popUpArticle.setTouchable(true);
        popUpArticle.setFocusable(true);
        popUpArticle.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popUpArticle.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popUpArticle.dismiss();
                layout_MainMenu.getForeground().setAlpha(0);
                click = true;
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
        popUpConnection.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popUpConnection.dismiss();
                layout_MainMenu.getForeground().setAlpha(0);
                click = true;
            }
        });
        layout_MainMenu = (FrameLayout) findViewById( R.id.mainmenu);
        layout_MainMenu.getForeground().setAlpha(0);
        //((FrameLayout) findViewById(R.id.action_bar)).getForeground().setAlpha(0);
        Button but = (Button) findViewById(R.id.loginButton);
        but.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (click) {
                    layout_MainMenu.getForeground().setAlpha(220);
                    //((FrameLayout) findViewById(R.id.action_bar)).getForeground().setAlpha(220);
                    popUpConnection.showAtLocation(findViewById(R.id.mainmenu), Gravity.CENTER, 0, 0);
                    popUpConnection.update(metrics.widthPixels - 100, metrics.heightPixels - 100);
                    click = false;
                } else {
                    //((FrameLayout) findViewById(R.id.action_bar)).getForeground().setAlpha(0);
                    //layout_MainMenu.getForeground().setAlpha(0);
                    popUpConnection.dismiss();
                    //click = true;
                }
            }
        });
        ((Button) popUpConnection.getContentView().findViewById(R.id.connect)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("connection en cours");
                handleConnection();
            }
        });
    }

    private void handlePopUpMyAccount() {
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
    }

    private void ifIsConnected() {
        findViewById(R.id.loginButton).setVisibility(View.GONE);
        findViewById(R.id.myAccountButton).setVisibility(View.VISIBLE);
        handlePopUpMyAccount();
    }

    private void handleConnection() {
        CharSequence email = ((TextView) popUpConnection.getContentView().findViewById(R.id.mail_connect)).getText();
        CharSequence mdp = ((TextView) popUpConnection.getContentView().findViewById(R.id.mdp_connect)).getText();
        //TODO : test if really connected
        connected = true;
        ifIsConnected();
        System.out.println("connexion réussie");
        popUpConnection.dismiss();
    }

    private void handleSlideMenu() {
        prepareListData();
        menuElementsList = (ExpandableListView) findViewById(R.id.menu_elements);
        menuElementsList.setGroupIndicator(getResources().getDrawable(R.drawable.group_indicator));
        menuElementsList.setMinimumWidth(metrics.widthPixels / 2);
        ExpandableListAdapter myAdapter = new ExpandableListAdapter(this, groupTitle, listDataChild,
                R.layout.menu, R.layout.menu_item, R.id.intitule, R.id.menu_element_title);
        menuLayout = (DrawerLayout) findViewById(R.id.menu_layout);
        // set a custom shadow that overlays the main content when the drawer opens
        //menuLayout.setDrawerShadow(R.drawable.airfree, GravityCompat.START);
        menuElementsList.setAdapter(myAdapter);
        menuElementsList.setOnItemClickListener(new DrawerItemClickListener());
        // enable ActionBar app icon to behave as action to toggle menu
        //mActionBar.setDisplayHomeAsUpEnabled(true);
        //mActionBar.setHomeButtonEnabled(true);

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
            }

            public void onDrawerOpened(View drawerView) {
                //invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                findViewById(R.id.search).setVisibility(View.VISIBLE);
                findViewById(R.id.title_text).setVisibility(View.GONE);
            }
        };
        menuLayout.setDrawerListener(menuToggle);
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

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        List<String> vendors = new ArrayList<String>();
        for(int k=0; k<bookTitle.length;k++) {
            vendors.add(bookTitle[k]);
        };
 
        listDataChild.put(groupTitle[0], vendors); // Header, Child data
        for(int k=1; k<groupTitle.length;k++) {
            listDataChild.put(groupTitle[k], new ArrayList<String>());
        };
        
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
        System.out.println("bouton appuyé");
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
            System.out.println("ok");
            System.out.println(id);
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

    private void pop(View Article){
        String t = Article.findViewById(R.id.intitule).toString();
        System.out.println(t);

    }
}
