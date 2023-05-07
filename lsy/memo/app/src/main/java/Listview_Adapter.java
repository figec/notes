
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.memo.R;

import org.w3c.dom.Text;

import java.util.List;



public class Listview_Adapter extends ArrayAdapter<Item> {
    private int resourceId;

    public Listview_Adapter(Context context, int textViewResourceId, List<Item> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Item item = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView content = (TextView) view.findViewById(R.id.item_string);
        content.setText(item.getContent());
        CheckBox check = (CheckBox) view.findViewById(R.id.ck);
        check.setChecked(item.getChecked());
        return view;


    }




}
