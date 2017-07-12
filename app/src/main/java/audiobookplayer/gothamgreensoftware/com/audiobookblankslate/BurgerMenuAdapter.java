package audiobookplayer.gothamgreensoftware.com.audiobookblankslate;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * TODO: doc
 * NOTE:  If, during the course of your application's life, you change the underlying data that is read by your adapter,
 * you should call notifyDataSetChanged(). This will notify the attached view that the data has been changed and it should refresh itself.
 * https://developer.android.com/reference/android/widget/ArrayAdapter.html#notifyDataSetChanged()
 *
 * Created by daryachernikhova on 7/11/17.
 */

public class BurgerMenuAdapter extends ArrayAdapter {
  private List<BurgerMenuItem> menuItems;


  /**
   * TODO
   * @param context
   * @param resource
   * @param objects
   */
  public BurgerMenuAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
    super(context, resource, objects);
    this.menuItems = objects;
  }


  /**
   * TODO:  make sure understanding is correct that invisible elements aren't counted in super.getCount
   * Supplements super.getCount, returns all elements, visible or no.
   * @return
   */
  public int getAllCount() {
    if (menuItems == null) {
      return 0;
    }
    return menuItems.size();
  }


  /**
   * Overrides the parent getView, so I can draw icons next to menu item strings.
   *
   * Thanks go to tutorial https://devtut.wordpress.com/2011/06/09/custom-arrayadapter-for-a-listview-android/
   *
   * @param position
   * @param convertView
   * @param parent
   * @return
   */
  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    //return super.getView(position, convertView, parent);

    // assign the view we are converting to a local variable
    View v = convertView;

    // first check to see if the view is null. if so, we have to inflate it.
    // to inflate it basically means to render, or show, the view.
    if (v == null) {
      LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      v = inflater.inflate(R.layout.burger_menu_drawer_item, null);
    }

    // NOTE:  the position may refer to the items in this adapter's list,
    // or it may refer to the items that are actually visible (being displayed
    // onscreen).  If so, could conceivably get into trouble when trying to get to
    // chapter 110, while chapters 1-4 are on-screen.
    // TODO: check and confirm
    BurgerMenuItem i = menuItems.get(position);

    if (i != null) {

      // This is how you obtain a reference to the TextViews.
      // These TextViews are created in the XML files we defined.
      TextView tt = (TextView) v.findViewById(R.id.burger_menu_item_text);
      // check to see if each individual textview is null.
      // if not, assign some text!
      if (tt != null){
        tt.setText(i.getText());
      }

      ImageView icon = (ImageView) v.findViewById(R.id.burger_menu_item_icon); //get id for image view
      icon.setImageResource(i.getIconResource());
    }

    // the view must be returned to our activity
    return v;
  }
}


// TODO:  got this message when running in emulator.  might be worth to check it out:  "07-11 20:32:16.874 10075-10075/audiobookplayer.gothamgreensoftware.com.audiobookblankslate I/Choreographer: Skipped 42 frames!  The application may be doing too much work on its main thread."


