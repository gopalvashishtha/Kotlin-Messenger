package com.gopal.kotlinmessenger.ui

import android.animation.Animator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isInvisible
import com.gopal.kotlinmessenger.Messages.LatestMessagesActivity
import com.gopal.kotlinmessenger.R
import kotlinx.android.synthetic.main.activity_splash_screen.*


class SplashScreenActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)





        lottie.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
                lottie.setAnimation("lottie.json")
                lottie.playAnimation()

            }
            override fun onAnimationEnd(animation: Animator?) {

            lottie.isInvisible = true
                val i = Intent(this@SplashScreenActivity, LatestMessagesActivity::class.java)
                startActivity(i)


            }
            override fun onAnimationStart(anim: Animator?) {



            }





            override fun onAnimationCancel(animation: Animator?) {

            }




        })

    }
}












