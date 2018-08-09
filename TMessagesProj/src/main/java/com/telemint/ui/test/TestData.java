package com.telemint.ui.test;

import com.telemint.ui.model.StakeTabMenu;
import com.telemint.ui.model.ValidatorList;
import com.telemint.ui.model.WalletTabMenu;

import java.util.ArrayList;

public class TestData {

    private static volatile TestData Instance = null;
    public static TestData getInstance() {
        TestData testDataInstance = Instance;
        if (testDataInstance == null) {
            synchronized (TestData.class) {
                testDataInstance = Instance;
                if (testDataInstance == null) {
                    Instance = testDataInstance = new TestData();
                }
            }
        }
        return testDataInstance;
    }


    public ArrayList<StakeTabMenu> getStakeTabMenu(){
        ArrayList<StakeTabMenu> list = new ArrayList<StakeTabMenu>();

        for(int i = 0; DummyVars.stakeTabTitles.length > i; i++){
            list.add(new StakeTabMenu(DummyVars.stakeTabTitles[i], DummyVars.stakeTabVars[i]));
        }

        return list;
    }

    public ArrayList<WalletTabMenu> getWalletTabMenu(){
        ArrayList<WalletTabMenu> list = new ArrayList<WalletTabMenu>();

        for(int i = 0; DummyVars.walletTabTitles.length > i; i++){
            list.add(new WalletTabMenu(DummyVars.walletTabTitles[i], DummyVars.walletTabVars[i]));
        }

        return list;
    }

    public ArrayList<ValidatorList> getValidatorList(){
        ArrayList<ValidatorList> list = new ArrayList<ValidatorList>();

        for(int i = 0; DummyVars.validatorNames.length > i; i++){
            list.add(new ValidatorList(DummyVars.validatorNames[i], DummyVars.validatorFees[i], DummyVars.validatorVotingPowers[i]));
        }

        return list;
    }
}
