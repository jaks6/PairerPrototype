package ee.ut.cs.mc.and.pairerprototype;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class GroupList extends ListView {
	String activeItem = "*";
	protected String TAG = GroupList.class.getName();
	public GroupList(Context context) {
		super(context);
		setClickListener(); 
	}
	private void setClickListener() {
		setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(final AdapterView<?> parent, View view,
			    int position, long id) {
				  
				  for (int j = 0; j < parent.getChildCount(); j++)
					  parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);

		            // change the background color of the selected element
		            view.setBackgroundColor(Color.LTGRAY);
				  Log.i(TAG , " item clicked, pos="+ position);
				  if (position == 0){
					  activeItem = "*";
				  } else {
					  activeItem = getItemAtPosition(position).toString();
				  }
			  }
			});
	}
	public GroupList(Context context, AttributeSet attrs) {
		super(context, attrs);
		setClickListener(); 
	}
	
	public String getRecipient(){
		return activeItem;
	}
	
}
