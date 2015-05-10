package io.boolio.android.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.boolio.android.R;
import io.boolio.android.models.Contact;

/**
 * Created by Chris on 5/9/15.
 */
public abstract class ContactsAdapter extends ArrayAdapter<Contact> {
    Context context;
    int resource;

    List<Contact> contacts;

    public ContactsAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.contacts = new ArrayList<>();
        loadContacts();
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Contact getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public void add(Contact object) {
        contacts.add(object);
    }

    Comparator<Contact> comparator = new Comparator<Contact>() {
        @Override
        public int compare(Contact lhs, Contact rhs) {
            return lhs.name.compareTo(rhs.name);
        }
    };

    @Override
    public void notifyDataSetChanged() {
        Collections.sort(contacts, comparator);
    }

    private void loadContacts() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER};

                Cursor people = context.getContentResolver().query(uri, projection, null, null, null);

                int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                people.moveToFirst();
                do {
                    add(new Contact(people.getString(indexName), people.getString(indexNumber)));
                } while (people.moveToNext());

                people.close();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContactHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_contacts, parent, false);
            holder = new ContactHolder();
            holder.name = (TextView) convertView.findViewById(R.id.contact_name);
            holder.add = convertView.findViewById(R.id.contact_add);

            convertView.setTag(holder);
        } else {
            holder = (ContactHolder) convertView.getTag();
        }

        final Contact contact = getItem(position);
        holder.name.setText(contact.name);
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleContactClick(contact.phone);
            }
        });

        return convertView;
    }

    private class ContactHolder {
        TextView name;
        View add;
    }

    public abstract void handleContactClick(String number);
}
