package com.cenah.buymeserver.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cenah.buymeserver.Interface.ItemClickListener;
import com.cenah.buymeserver.R;
import com.cenah.buymeserver.ViewHolder.ProductsViewHolder;
import com.cenah.buymeserver.models.Category;
import com.cenah.buymeserver.models.Common;
import com.cenah.buymeserver.models.Products;
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

public class ClotheListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private StorageReference storageReference;
    private DatabaseReference productList;


    private String categoryId;
    private FirebaseRecyclerAdapter<Products, ProductsViewHolder> adapter;

    private EditText ed_name, ed_descount, ed_price, ed_desc;
    private Button btn_select, btn_upload;

    private Uri saveUri;
    private Products newProduct;
    private final int PICL_IMAGE_REQUEST = 77;
    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothe_list);

        FloatingActionButton fab = findViewById(R.id.fab);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        productList = db.getReference("Products");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        layout = findViewById(R.id.layout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (getIntent() != null) categoryId = getIntent().getStringExtra("CatId");
        if (categoryId != null && !categoryId.isEmpty()) LoadList(categoryId);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        newProduct = null;
        saveUri = null;
        AlertDialog.Builder alertDailog = new AlertDialog.Builder(ClotheListActivity.this);
        alertDailog.setTitle("Add new");
        alertDailog.setMessage("Fill in all fields!");
        LayoutInflater inflater = this.getLayoutInflater();
        View add_item = inflater.inflate(R.layout.add_new_product, null);
        ed_name = add_item.findViewById(R.id.ed_name);
        btn_select = add_item.findViewById(R.id.btn_select);
        btn_upload = add_item.findViewById(R.id.btn_upload);
        ed_descount = add_item.findViewById(R.id.ed_descount);
        ed_price = add_item.findViewById(R.id.ed_price);
        ed_desc = add_item.findViewById(R.id.ed_desc);
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

                if (newProduct != null) {
                    productList.push().setValue(newProduct);
                    Snackbar.make(layout, "New category " + newProduct.getName() + " added", Snackbar.LENGTH_SHORT).show();
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

    private void LoadList(String categoryId) {

        adapter = new FirebaseRecyclerAdapter<Products, ProductsViewHolder>(Products.class, R.layout.product_item, ProductsViewHolder.class, productList.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(ProductsViewHolder viewHolder, Products model, int position) {
                viewHolder.tx_name.setText(model.getName().trim());
                viewHolder.tx_about.setText(model.getDescription().trim());
                viewHolder.tx_price.setText("Price: "+model.getPrice());
                viewHolder.tx_descount.setText("Discount: "+model.getDiscount());

                Picasso.get().load(model.getImage()).into(viewHolder.iv_image);

                final Products local = model;

                viewHolder.setItemClickListner(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int pos, boolean isLongClick) {

                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void uploadImage() {
        if (saveUri != null && !ed_name.getText().toString().isEmpty() && !ed_desc.getText().toString().isEmpty() && !ed_price.getText().toString().isEmpty() && !ed_descount.getText().toString().isEmpty()) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Loading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(ClotheListActivity.this, "Uploaded !!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // set value for cat if image uploaded and we can get download link
                            newProduct = new Products();
                            newProduct.setName(ed_name.getText().toString());
                            newProduct.setDescription(ed_desc.getText().toString());
                            newProduct.setPrice(ed_price.getText().toString());
                            newProduct.setDiscount(ed_descount.getText().toString());
                            newProduct.setMenuId(categoryId);
                            newProduct.setImage(uri.toString());

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(ClotheListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Loading " + progress + "%");

                }
            });
        } else {
            Toast.makeText(ClotheListActivity.this, "Fill in all fields!", Toast.LENGTH_SHORT).show();

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

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.DELETE)) {
            deleteCat(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
            Toast.makeText(ClotheListActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();

        } else if (item.getTitle().equals(Common.UPDATE)) {

            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }

        return super.onContextItemSelected(item);
    }

    private void deleteCat(String key, Products item) {
        productList.child(key).removeValue();
    }

    private void showUpdateDialog(final String key, final Products item) {
        saveUri = null;
        AlertDialog.Builder alertDailog = new AlertDialog.Builder(ClotheListActivity.this);
        alertDailog.setTitle("update catalog");
        alertDailog.setMessage("Fill in all fields!");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_item = inflater.inflate(R.layout.add_new_product, null);

        ed_name = add_item.findViewById(R.id.ed_name);
        btn_select = add_item.findViewById(R.id.btn_select);
        btn_upload = add_item.findViewById(R.id.btn_upload);

        ed_descount = add_item.findViewById(R.id.ed_descount);
        ed_price = add_item.findViewById(R.id.ed_price);
        ed_desc = add_item.findViewById(R.id.ed_desc);

        alertDailog.setView(add_item);

        ed_name.setText(item.getName());
        ed_descount.setText(item.getDiscount());
        ed_price.setText(item.getPrice());
        ed_desc.setText(item.getDescription());

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
                item.setDiscount(ed_descount.getText().toString());
                item.setPrice(ed_price.getText().toString());
                item.setDescription(ed_desc.getText().toString());
                productList.child(key).setValue(item);
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

    private void changeImage(final Products item) {
        if (saveUri != null && !ed_name.getText().toString().isEmpty() && !ed_desc.getText().toString().isEmpty() && !ed_price.getText().toString().isEmpty() && !ed_descount.getText().toString().isEmpty()) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(ClotheListActivity.this,"Uploaded !!",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ClotheListActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Uploading "+ progress+"%");

                }
            });
        }else {
            Toast.makeText(ClotheListActivity.this,"Fill in all fields!",Toast.LENGTH_SHORT).show();

        }
    }


}
