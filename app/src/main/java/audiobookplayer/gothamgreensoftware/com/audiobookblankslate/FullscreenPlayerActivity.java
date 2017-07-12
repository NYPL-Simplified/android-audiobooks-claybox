package audiobookplayer.gothamgreensoftware.com.audiobookblankslate;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FullscreenPlayerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

  private final String TAG = "AudiobookBlankSlate";
  private final String SUB_TAG = "FullscreenPlayerActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_fullscreen_player);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    /* darya:  this is left over from the sample app, but is a nice example of a floating button.  keep for now.
    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
      }
    });
    */

    // drawer_layout is defined in activity_fullscreen_player.xml, and contains our drawer ListView
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    /* NOTE: v22v code:
    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    */

    // v19 code:
    ListView navigationView = (ListView) findViewById(R.id.nav_view);
    List<BurgerMenuItem> menuItems = new ArrayList<BurgerMenuItem>();

    // TODO:  take the hardcoded stuff outside
    // TODO:  unhardcode the strings, so they can be translatable
    BurgerMenuAdapter menuAdapter = new BurgerMenuAdapter(this, R.layout.burger_menu_drawer_item, menuItems);
    BurgerMenuItem menuItem = new BurgerMenuItem(R.drawable.menu_icon_catalog, "SimplyE");
    menuAdapter.add(menuItem);
    menuItem = new BurgerMenuItem(R.drawable.menu_icon_books, "My Books");
    menuAdapter.add(menuItem);
    menuItem = new BurgerMenuItem(R.drawable.menu_icon_settings, "Settings");
    menuAdapter.add(menuItem);
    menuItem = new BurgerMenuItem(R.drawable.ic_info_outline_black_24dp, "Book Info");
    menuAdapter.add(menuItem);
    menuItem = new BurgerMenuItem(R.drawable.ic_radio_button_unchecked_black_24dp, "Chapter 1");
    menuAdapter.add(menuItem);
    menuItem = new BurgerMenuItem(R.drawable.ic_radio_button_checked_black_24dp, "Chapter 2");
    menuAdapter.add(menuItem);


    navigationView.setAdapter(menuAdapter);

    // click listener for the burger menu items
    navigationView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
        // TODO: call my drawing and playing method, passing it the position and the view
        Log.d(TAG, SUB_TAG + "Menu item clicked");
        BurgerMenuItem value = (BurgerMenuItem) parentAdapter.getItemAtPosition(position);
        //String main = navigationView.getSelectedItem().toString();
        BurgerMenuAdapter burgerMenuAdapter = (BurgerMenuAdapter) parentAdapter.getAdapter();
        int iNum = burgerMenuAdapter.getCount();

        /*
    for(int i=0; i<iNum; i++)
    {
        Cursor c = (Cursor) adapter.getItem(i);

        // Now you can pull data from the cursor object,
        // if that's what you used to create the adapter to start with
    }
EDIT: In response to jeffamaphone's comments, here's something else... if you are trying to work with each UI element then getChildAt is certainly more appropriate as it returns the View for the sub-item, but in general you can still only work with those that are visible at the time. If that's all you care about, then fine - just make sure you check for null when the call returns.

where visible means off-screen, which can totally happen with chapters

If you are trying to implement something like I was - a "Select All / Select None / Invert Selection" type of feature for a list that might exceed the screen, then you are much better off to make the changes in the Adapter, or have an external array (if as in my case, there was nowhere in the adapter to make the chagne), and then call notifyDataSetChanged() on the List Adapter. For example, my "Invert" feature has code like this:

    case R.id.selectInvertLedgerItems:
        for(int i=0; i<ItemChecked.length; i++)
        {
            ItemChecked[i] = !ItemChecked[i];
        }
        la.notifyDataSetChanged();
        RecalculateTotalSelected();
        break;
Note that in my case, I am also using a custom ListView sub-item, using adapter.setViewBinder(this); and a custom setViewValue(...) function.

Furthermore if I recall correctly, I don't think that the "position" in the list is necessarily the same as the "position" in the adapter... it is again based more on the position in the list. Thus, even though you are wanting the "50th" item on the list, if it is the first visible, getChildAt(50) won't return what you are expecting. I think you can use ListView.getFirstVisiblePosition() to account and adjust.

         */

      }
    });

  }


  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.top_menu_settings_selector, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_simplye) {
      // do nothing
    } else if (id == R.id.nav_my_books) {
      // do nothing
    } else if (id == R.id.nav_settings) {
      // do nothing

    } else if (id == R.id.nav_book_info) {
      // show book detail activity
    } else if (id == R.id.nav_chapter_1) {
      // TODO: unhardcode chapter selection by refactoring the "if in chapter group" check into its own method and calling instead of asking for chapter id

      // TODO for that matter, having the radion buttons might be cluttery, and maybe don't want the icons for chapters at all.

      // set the other menu selections to "unchecked"
      /* NOTE: v22v code:
      NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

      Menu navMenu = navigationView.getMenu();
      int menuSize = navMenu.size();
      for (int i=0; i<menuSize; i++) {
        MenuItem menuItem = navMenu.getItem(i);
        // unselect the menu item if it's not our selected one
        // NOTE: if doesn't work, try invisible menu item, like in https://stackoverflow.com/questions/36051045/how-to-uncheck-checked-items-in-navigation-view/39017882
        if (menuItem.getItemId() != id) {
          menuItem.setChecked(false);
        }

        int groupId = menuItem.getGroupId();
        if (groupId == R.id.group_chapter_menu) {
          // if menu item has a radio button icon, draw an empty unchecked one
          item.setIcon(R.drawable.ic_radio_button_unchecked_black_24dp);
        }
      }

      // set the chapter 1 radio button to "checked"
      item.setChecked(true);
      navigationView.setCheckedItem(id);
      item.setIcon(R.drawable.ic_radio_button_checked_black_24dp);
      */

      // v19 code:
      ListView navigationView = (ListView) findViewById(R.id.nav_view);
      //navigationView.


      // TODO play the audio for chapter 1
      playStuff();
    }// if chapter


    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }// burger menu listener


  /**
   * TODO:  must wrap MediaPlayer in my own implementation to keep track of state when the
   * activity that starts the player stops and resumes (https://stackoverflow.com/questions/11876229/android-mediaplayer-is-there-an-isprepared-or-getstatus-method).
   */
  public void playStuff() {

    // TODO remove mo3 file from res/raw starts with a number

    // TODO mp3 file location is currently hardcoded to assets dir, but in SimplyE will be calling a URI that's outside of the apk
    String chapterFilePath = "21_gun_salute/1752599_001_C001.mp3";

    //Uri myUri = Uri.parse("file:///android_asset/a21_gun_salute/a1752599_001_c001.mp3"); // initialize Uri here

    AssetManager assetManager = getResources().getAssets();
    AssetFileDescriptor assetFileDescriptor = null;

    MediaPlayer mediaPlayer = new MediaPlayer();
    // so we can call media playback asynchronously, so's not to hang the UI thread
    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
      @Override
      public void onPrepared(MediaPlayer mp) {
        mp.start();
      }
    });


    try {
      assetFileDescriptor = assetManager.openFd(chapterFilePath);
      mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
      mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());

      //mediaPlayer.prepare(); // synchronously
      mediaPlayer.prepareAsync();
    } catch (IOException e) {
      // TODO
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      // TODO
      e.printStackTrace();
    } finally {
      if (assetFileDescriptor != null) {
        try {
          assetFileDescriptor.close();
        } catch (IOException e) {
          // TODO
          e.printStackTrace();
        }
      }
    }

    mediaPlayer.start();
  }

}
