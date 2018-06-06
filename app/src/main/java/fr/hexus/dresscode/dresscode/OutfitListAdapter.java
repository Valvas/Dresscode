package fr.hexus.dresscode.dresscode;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OutfitListAdapter extends ArrayAdapter<Outfit>
{
    public OutfitListAdapter(@NonNull Context context, List<Outfit> outfits)
    {
        super(context, 0, outfits);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View row = inflater.inflate(R.layout.activity_wardrobe_outfit, null);

        Outfit currentOutfit = getItem(position);

        TextView outfitName = row.findViewById(R.id.outfitName);
        TextView outfitElements = row.findViewById(R.id.outfitElements);

        outfitName.setText(currentOutfit.getName());
        outfitElements.setText(getContext().getResources().getString(R.string.amount_of_elements_in_outfit) + " : " + currentOutfit.getElements().size());

        return row;
    }
}
