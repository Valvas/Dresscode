package fr.hexus.dresscode.dresscode;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class WardrobeElementAdapter extends ArrayAdapter<WardrobeElement>
{
    public WardrobeElementAdapter(@NonNull Context context, List<WardrobeElement> wardrobeElements)
    {
        super(context, 0, wardrobeElements);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View row = inflater.inflate(R.layout.activity_wardrobe_element_adapter, null);

        WardrobeElement currentWardrobeElement = getItem(position);

        TextView wardrobeElementName = row.findViewById(R.id.wardrobeElementName);
        TextView wardrobeElementType = row.findViewById(R.id.wardrobeElementType);

        wardrobeElementName.setText(String.valueOf(currentWardrobeElement.getName()));
        wardrobeElementType.setText(String.valueOf(currentWardrobeElement.getType()));

        return row;
    }
}
