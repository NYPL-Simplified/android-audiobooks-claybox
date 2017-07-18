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

import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.MenuItem;
import android.view.View;

import android.widget.MediaController.MediaPlayerControl;


public class FullscreenPlayerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MediaPlayerControl {

  // so can filter all log msgs belonging to my app
  private final String TAG = "AudiobookBlankSlate";
  // so can do a search in log msgs for just this class's output
  private final String SUB_TAG = "FullscreenPlayerActivity";

  // we will play audio in the MusicService
  private MusicService musicService = null;
  private Intent playIntent = null;
  private boolean isMusicServiceBound = false;

  private ArrayList<ChapterTrack> chapters = null;


  /* ---------------------------------- LIFECYCLE METHODS ----------------------------------- */

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

    // TODO:  fill in, then generate the BurgerMenuItem off it, then pass in from manifest-reading code
    chapters = new ArrayList<ChapterTrack>();

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

    // TODO: integrate with onNavigationItemSelected, which used to handle the menu clicks when the menu was in the NavigationView
    // (it might be there again, once I wrap it, will the clicks come to both methods?)
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

        if (musicService != null) {
          musicService.setChapterPosition(0);
          musicService.playTrack();
        } else {
          Log.d(TAG, SUB_TAG + "why?");
        }
      }
    });

  }


  /**
   * Stop the media playback when this activity is destroyed, and clean up resources.
   * TODO:  Might want to modify this later, so that audio can keep playing when
   * user navigates away from activity, and activity is garbage collected.  Instead,
   * stop media playback service only when the SimplyE app is closed.
   * Do find out what resources should stop/release so SimplyE doesn't hog when user isn't listening to an audiobook.
   */
  @Override
  protected void onDestroy() {
    stopService(playIntent);
    musicService = null;
    super.onDestroy();
  }


  /**
   * Start the MusicService instance when the Activity instance starts.
   * Pass the song list we've assembled to the MusicService.
   *
   *
   */
  @Override
  protected void onStart() {
    super.onStart();
    Log.d(TAG, SUB_TAG + "Activity.onStart");

    if (playIntent == null) {
      playIntent = new Intent(this, MusicService.class);
      // BIND_AUTO_CREATE recreates the Service if it is destroyed when there’s a bounding client
      boolean bound = bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
      Log.d(TAG, SUB_TAG + "bound=" + bound);

      // TODO: ?? Every call to this method will result in a corresponding call to the target service's android.app.Service.onStartCommand method,
      // with the intent given here. This provides a convenient way to submit jobs to a service without having to bind and call on to its interface.
      startService(playIntent);
    }
  }


  /**
   * We are going to play the music in the Service class, but control it from the Activity class, where the application's user interface operates.
   * To accomplish this, we will have to bind to the Service class, which we do here.
   */
  private ServiceConnection musicConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      Log.d(TAG, SUB_TAG + "musicConnection.onServiceConnected");

      MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
      // get service
      musicService = binder.getService();

      // pass chapter list
      // NOTE: If the service is Local (not IntentService), setChapters execution happens on the thread of the calling client/activity (this UI thread).
      // If wish to call a long-running operation, then spin a new background thread in the called service method.
      musicService.setChapters(chapters);
      isMusicServiceBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      musicService = null;
      isMusicServiceBound = false;
    }
  };


  /* ---------------------------------- /LIFECYCLE METHODS ----------------------------------- */


  /* ------------------------------------ NAVIGATION EVENT HANDLERS ------------------------------------- */

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


      musicService.setChapterPosition(0);
      musicService.playTrack();
    }// if chapter


    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }// burger menu listener

  /* ------------------------------------ /NAVIGATION EVENT HANDLERS ------------------------------------- */


  /* ------------------------------------ PLAYBACK EVENT HANDLERS ------------------------------------- */

  @Override
  public void start() {

  }

  @Override
  public void pause() {

  }

  @Override
  public int getDuration() {
    return 0;
  }

  @Override
  public int getCurrentPosition() {
    return 0;
  }

  @Override
  public void seekTo(int pos) {

  }

  @Override
  public boolean isPlaying() {
    return false;
  }

  @Override
  public int getBufferPercentage() {
    return 0;
  }

  @Override
  public boolean canPause() {
    return false;
  }

  @Override
  public boolean canSeekBackward() {
    return false;
  }

  @Override
  public boolean canSeekForward() {
    return false;
  }

  @Override
  public int getAudioSessionId() {
    return 0;
  }

  /* ------------------------------------ /PLAYBACK EVENT HANDLERS ------------------------------------- */


}
