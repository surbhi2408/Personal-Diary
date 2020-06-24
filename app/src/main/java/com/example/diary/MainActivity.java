package com.example.diary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.diary.authentication.Login;
import com.example.diary.authentication.Register;
import com.example.diary.model.Adapter;
import com.example.diary.model.Note;
import com.example.diary.note.EditNote;
import com.example.diary.note.NoteDetails;
import com.example.diary.note.WriteNotes;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    RecyclerView noteLists;
    Adapter adapter;
    DrawerLayout drawer;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    FirebaseFirestore fstore;
    FirestoreRecyclerAdapter<Note,NoteViewHolder> noteAdapter;
    FirebaseUser user;
    FirebaseAuth fAuth;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fstore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        Query query = fstore.collection("notes").document(user.getUid()).collection("myNotes").orderBy("title",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query,Note.class)
                .build();

        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, final int i, @NonNull final Note note) {

                noteViewHolder.noteTitle.setText(note.getTitle());
                noteViewHolder.noteContent.setText(note.getContent());
                final int code = getRandomColor();
                noteViewHolder.cardView.setCardBackgroundColor(noteViewHolder.view.getResources().getColor(code,null));
                final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();

                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), NoteDetails.class);
                        intent.putExtra("title",note.getTitle());
                        intent.putExtra("content",note.getContent());
                        intent.putExtra("code",code);
                        intent.putExtra("noteId",docId);
                        v.getContext().startActivity(intent);
                    }
                });

                ImageView menuIcon = noteViewHolder.view.findViewById(R.id.menuIcon);
                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();
                        PopupMenu menu = new PopupMenu(v.getContext(),v);
                        menu.setGravity(Gravity.END);
                        menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent intent = new Intent(v.getContext(), EditNote.class);
                                intent.putExtra("title",note.getTitle());
                                intent.putExtra("content",note.getContent());
                                intent.putExtra("noteId",docId);
                                startActivity(intent);
                                return false;
                            }
                        });

                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference docref = fstore.collection("notes").document(user.getUid()).collection("myNotes").document(docId);
                                docref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // note deleted
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Error in deleting note.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        menu.show();
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout,parent,false);
                return new NoteViewHolder(view);
            }
        };

        FloatingActionButton fab = findViewById(R.id.fab);

        noteLists = findViewById(R.id.notelist);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), WriteNotes.class));
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                finish();
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.open,R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.notes, R.id.addnotes, R.id.sync,
//                R.id.rating, R.id.shareApp, R.id.logout)
//                .setDrawerLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);


//        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
//            @Override
//            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
//
//                int menuId = destination.getId();
//
//                switch (menuId){
//                    case R.id.addnotes:
//                        startActivity(new Intent(MainActivity.this, WriteNotes.class));
//                        fab.hide();
//                        break;
//                    default:
//                        fab.show();
//                        break;
//                }
//            }
//        });
        noteLists.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        noteLists.setAdapter(noteAdapter);

        View headerView = navigationView.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.userDisplayName);
        TextView userEmail = headerView.findViewById(R.id.userDisplayEmail);

        if(user.isAnonymous())
        {
            userEmail.setVisibility(View.GONE);
            username.setText("Temporary User");
        }
        else
        {
            userEmail.setText(user.getEmail());
            username.setText(user.getDisplayName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this,Settings.class));
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
   public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
       switch (item.getItemId()) {
           case R.id.addnotes:
               startActivity(new Intent(MainActivity.this, WriteNotes.class));
               overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
               finish();
               break;
           case R.id.sync:
               if(user.isAnonymous())
               {
                   startActivity(new Intent(MainActivity.this,Login.class));
                   overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                   finish();
               }
               else
               {
                   Toast.makeText(this, "You are Connected.", Toast.LENGTH_SHORT).show();
               }
               break;
           case R.id.logout:
               checkUser();
               break;
           default:
               Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
       }
       return false;
   }

    private void checkUser() {
        if(user.isAnonymous())
        {
            displayAlert();
        }
        else
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),SplashActivity.class));
            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
            finish();
        }
    }

    private void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("You are logged in with Temporary account. Logged out will delete all the notes.")
                .setPositiveButton("Sync Note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        finish();
                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(),SplashActivity.class));
                                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                                finish();
                            }
                        });
                    }
                });
        warning.show();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder
   {
       TextView noteTitle,noteContent;
       View view;
       CardView cardView;

       public NoteViewHolder(@NonNull View itemView) {
           super(itemView);
           noteTitle = itemView.findViewById(R.id.titles);
           noteContent = itemView.findViewById(R.id.content);
           cardView = itemView.findViewById(R.id.noteCard);
           view = itemView;
       }
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
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }
}
