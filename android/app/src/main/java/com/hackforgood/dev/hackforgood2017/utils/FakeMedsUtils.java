package com.hackforgood.dev.hackforgood2017.utils;

import com.hackforgood.dev.hackforgood2017.model.Medicine;

/**
 * Created by LaQuay on 15/09/2017.
 */

public class FakeMedsUtils {
    public static final int DUMMY_AMOXICILINA = 0;
    public static final int DUMMY_CETRAXAL = 1;
    public static final int DUMMY_BUDESONIDA_ALCON = 2;

    public static Medicine getDummyMedicine(int dummyMedCode) {
        Medicine medicine = new Medicine();
        switch (dummyMedCode) {
            case DUMMY_AMOXICILINA:
                medicine.setName("Amoxicilina/Ácido clavulánico Mylan 500 mg/125 mg...");
                medicine.setCode(694513);
                medicine.setImageURL("https://image.prntscr.com/image/QBpIhPXYSu6TKTjyl_81gg.png");

                break;
            case DUMMY_CETRAXAL:
                medicine.setName("Cetraxal Otico CIPROFLOXACINO GOTAS OTICAS EN SOLUCION 10 ml");
                medicine.setCode(682617);
                medicine.setImageURL("https://image.prntscr.com/image/qBYxxpEmTv_c2yT_bFHPzw.png");

                break;
            case DUMMY_BUDESONIDA_ALCON:
                medicine.setName("Budesonida Alcon 100 microgramos/dosis para suspensión para pulverización nasal");
                medicine.setCode(738278);
                medicine.setImageURL("https://image.prntscr.com/image/G2DLKdCRSAOotiXTBcpLuw.png");

                break;
        }

        return medicine;
    }
}
