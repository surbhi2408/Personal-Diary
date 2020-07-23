package com.example.diary.model;

import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diary.R;
import com.example.diary.note.NoteDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements Filterable
{
    List<String> titles;
    List<String> content;
    //ImageView imageView;
    private List<Note> notes;
    private List<Note> noteSource;
    //private Timer timer;

    public Adapter(List<Note> notes) {
        this.notes = notes;
        this.noteSource = new ArrayList<>(notes);
    }

    public Adapter(List<String> title, List<String> content)
    {
        this.titles = title;
        this.content = content;
        //this.notes = notes;
        //this.noteSource = noteSource;
        //this.imageView = imageView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout,parent,false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.noteTitle.setText(titles.get(position));
        holder.noteContent.setText(content.get(position));
        final int code = getRandomColor();
        holder.cardView.setCardBackgroundColor(holder.view.getResources().getColor(code,null));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NoteDetails.class);
                intent.putExtra("title",titles.get(position));
                intent.putExtra("content",content.get(position));
                intent.putExtra("code",code);
                v.getContext().startActivity(intent);
            }
        });
    }

    private int getRandomColor()
    {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.blue);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.notgreen);
        colorCode.add(R.color.skyblue);
        colorCode.add(R.color.lightPurple);
        colorCode.add(R.color.lightGreen);
        colorCode.add(R.color.gray);
        colorCode.add(R.color.pink);
        colorCode.add(R.color.greenlight);
        colorCode.add(R.color.red);
        colorCode.add(R.color.dirtygreen);

        Random randomColor = new Random();
        int number = randomColor.nextInt(colorCode.size());
        return colorCode.get(number);
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView noteTitle,noteContent;
        //ImageView noteImage;
        View view;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);
            //noteImage = itemView.findViewById(R.id.imageNoteView);
            cardView = itemView.findViewById(R.id.noteCard);
            view = itemView;
        }
    }

//    public void setFilter(List<Note> FilteredDataList)
//    {
//        notes = FilteredDataList;
//        notifyDataSetChanged();
//    }

//    public void searchNotes(final String searchKeyword)
//    {
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if(searchKeyword.trim().isEmpty())
//                {
//                    notes = noteSource;
//                }
//                else
//                {
//                    ArrayList<Note> tmp = new ArrayList<>();
//                    for(Note note : noteSource)
//                    {
//                        if(note.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
//                                || note.getContent().toLowerCase().contains(searchKeyword.toLowerCase()))
//                        {
//                            tmp.add(note);
//                        }
//                    }
//                    notes = tmp;
//                }
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        notifyDataSetChanged();
//                    }
//                });
//            }
//        },500);
//    }
//
//    public void cancelTimer()
//    {
//        if(timer != null)
//        {
//            timer.cancel();
//        }
//    }

    @Override
    public Filter getFilter() {
        return noteFilter;
    }

    private Filter noteFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Note> filteredList = new ArrayList<>();
            if(constraint.toString().isEmpty())
            {
                filteredList.addAll(noteSource);
            }
            else
            {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Note item : noteSource)
                {
                    if(item.getTitle().toLowerCase().contains(filterPattern) || item.getContent().toLowerCase().contains(filterPattern))
                    {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notes.clear();
            notes.addAll((Collection<? extends Note>) results.values);
            notifyDataSetChanged();
        }
    };
}
