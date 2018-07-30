package xyz.lebalex.mysongs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SongListAdapter extends BaseAdapter {
    private List<SongModel> listSongModel;
    private LayoutInflater mLayoutInflater;

    public SongListAdapter(Context context, List<SongModel> listSongModel) {
        this.listSongModel = listSongModel;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listSongModel.size();
    }

    @Override
    public Object getItem(int i) {
        return listSongModel.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if(view==null)
        {
            view = mLayoutInflater.inflate(R.layout.list_layout, viewGroup, false);
        }
        SongModel song = getSong(i);
        TextView songName = (TextView) view.findViewById(R.id.songName);
        songName.setText(song.getSongName());

        return view;
    }
    private SongModel getSong(int i)
    {
        return (SongModel) getItem(i);
    }

}
