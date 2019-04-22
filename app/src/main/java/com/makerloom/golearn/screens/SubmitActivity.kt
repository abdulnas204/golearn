package com.makerloom.golearn.screens

import mehdi.sakout.fancybuttons.FancyButton

interface SubmitActivity {
    fun disableBtn (btn: FancyButton) {
        btn.isEnabled = false
    }

    fun enableBtn (btn: FancyButton) {
        btn.isEnabled = true
    }
}