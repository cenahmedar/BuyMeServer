package com.cenah.buymeserver.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cenah.buymeserver.Interface.ItemClickListener;
import com.cenah.buymeserver.R;
import com.cenah.buymeserver.Services.ListenService;
import com.cenah.buymeserver.ViewHolder.MenuViewHolder;
import com.cenah.buymeserver.models.Category;
import com.cenah.buymeserver.models.Common;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;


    private DatabaseReference catagories;
    private FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    private StorageReference storageReference;

    private EditText ed_name;
    private Button btn_select, btn_upload;
    private Category newCategory;
    private Uri saveUri;
    private final int PICL_IMAGE_REQUEST = 77;
    private DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu settings");
        setSupportActionBar(toolbar);

        //init firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        catagories = database.getReference("Category");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //set name for user
        View view = navigationView.getHeaderView(0);
        TextView hd_name = view.findViewById(R.id.hd_name);
        hd_name.setText(Common.currnetUser.getName());


        //Load menu
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        LoadMenu();

        Intent service = new Intent(getApplicationContext(), ListenService.class);
        startService(service);


    }

    private void showDialog() {
        newCategory = null;
        saveUri = null;
        AlertDialog.Builder alertDailog = new AlertDialog.Builder(HomeActivity.this);
        alertDailog.setTitle("Add new catalog");
        alertDailog.setMessage("Fill in all fields!");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_item = inflater.inflate(R.layout.add_new, null);

        ed_name = add_item.findViewById(R.id.ed_name);
        btn_select = add_item.findViewById(R.id.btn_select);
        btn_upload = add_item.findViewById(R.id.btn_upload);

        alertDailog.setView(add_item);

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });


        alertDailog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (newCategory != null) {
                    catagories.push().setValue(newCategory);
                    Snackbar.make(drawer, "New catagory " + newCategory.getName() + " eklendi", Snackbar.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });
        alertDailog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        alertDailog.show();
    }

    private void uploadImage() {
        if (saveUri != null && !ed_name.getText().toString().isEmpty()) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Loading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(HomeActivity.this, "Uploaded !!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // set value for cat if image uploaded and we can get download link
                            newCategory = new Category(ed_name.getText().toString(), uri.toString());

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Loading " + progress + "%");

                }
            });
        } else {
            Toast.makeText(HomeActivity.this, "Fill in all fields!", Toast.LENGTH_SHORT).show();

        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), PICL_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICL_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            saveUri = data.getData();
            btn_select.setText("PICTURE SELECTED");
        }
    }

    private void LoadMenu() {
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class, catagories) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {
                viewHolder.menu_name.setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.menu_image);

                viewHolder.setItemClickListner(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLong) {
                        Intent intent = new Intent(HomeActivity.this, ClotheListActivity.class);
                        intent.putExtra("CatId", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh)
            LoadMenu();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_orders) {
            startActivity(new Intent(HomeActivity.this, OrderStatusActivity.class));

        } else if (id == R.id.nav_log_put) {
            Intent intent = new Intent(getApplicationContext(), SignActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.DELETE)) {
            deleteCat(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
            Toast.makeText(HomeActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();

        } else if (item.getTitle().equals(Common.UPDATE)) {

            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }

        return super.onContextItemSelected(item);
    }

    private void deleteCat(String key, Category item) {
        catagories.child(key).removeValue();
    }

    private void showUpdateDialog(final String key, final Category item) {
        saveUri = null;
        AlertDialog.Builder alertDailog = new AlertDialog.Builder(HomeActivity.this);
        alertDailog.setTitle("update catalog");
        alertDailog.setMessage("Fill in all fields!");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_item = inflater.inflate(R.layout.add_new, null);

        ed_name = add_item.findViewById(R.id.ed_name);
        btn_select = add_item.findViewById(R.id.btn_select);
        btn_upload = add_item.findViewById(R.id.btn_upload);


        alertDailog.setView(add_item);

        ed_name.setText(item.getName());
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });


        alertDailog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                item.setName(ed_name.getText().toString());
                catagories.child(key).setValue(item);
            }
        });
        alertDailog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        alertDailog.show();
    }

    private void changeImage(final Category item) {

        if (saveUri != null && !ed_name.getText().toString().isEmpty()) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Loading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(HomeActivity.this, "Uploaded !!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // set value for cat if image uploaded and we can get download link
                            item.setImage(uri.toString());

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Loading " + progress + "%");

                }
            });
        } else {
            Toast.makeText(HomeActivity.this, "Bütün alanları doldur!", Toast.LENGTH_SHORT).show();

        }
    }
}
