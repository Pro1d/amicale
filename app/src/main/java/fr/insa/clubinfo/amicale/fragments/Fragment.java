package fr.insa.clubinfo.amicale.fragments;

import fr.insa.clubinfo.amicale.interfaces.OnBackPressedListener;

/**
 * Created by Proïd on 10/06/2016.
 */

public abstract class Fragment extends android.app.Fragment implements OnBackPressedListener {
    @Override
    public boolean onBackPressed() {
        return false;
    }
}
