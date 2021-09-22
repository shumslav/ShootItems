package com.example.myapplication3.UIelements.Fragments

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.core.view.forEach
import com.example.myapplication3.R
import com.example.myapplication3.Utils.images

class Game : Fragment() {

    private lateinit var leftWeapon: ImageView
    private lateinit var rightWeapon: ImageView
    private lateinit var board: FrameLayout
    private lateinit var spawnTimer: CountDownTimer
    private lateinit var livesLayout: RelativeLayout
    private lateinit var scoreText: TextView
    private lateinit var bonus: ImageView
    private lateinit var bulletsLayout:RelativeLayout
    private lateinit var resultLayout:RelativeLayout
    private lateinit var resultScoreText:TextView
    private lateinit var backImage:ImageView

    private val allProjectile = mutableListOf<Projectile>()
    private val livesList = mutableListOf<ImageView>()
    private val bulletsList = mutableListOf<ImageView>()

    private var isLeftWeaponShoot = false
    private var rateSpawn = 200
    private var score = 0
    private var countBonus = 0
    private var isCanFire = true
    private var bullets = 8

    private var live = 3

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game, container, false)

        leftWeapon = view.findViewById(R.id.left_weapon)
        rightWeapon = view.findViewById(R.id.right_weapon)
        board = view.findViewById(R.id.board)
        livesLayout = view.findViewById(R.id.lives)
        scoreText = view.findViewById(R.id.score)
        bonus = view.findViewById(R.id.bonus)
        bulletsLayout = view.findViewById(R.id.bullets_layout)
        resultLayout = view.findViewById(R.id.result_layout)
        resultScoreText = view.findViewById(R.id.result_score)
        backImage = view.findViewById(R.id.back)

        bulletsLayout.forEach { bulletsList.add(it as ImageView) }

        livesLayout.forEach { livesList.add(it as ImageView) }

        backImage.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .remove(this)
                .commit()
            requireActivity().supportFragmentManager.popBackStack()
        }

        board.setOnClickListener {
            if (!isCanFire)
                return@setOnClickListener
            shootWeapon()
        }

        spawnTimer = object : CountDownTimer(10, 1000) {
            var thisRate = rateSpawn
            override fun onTick(millisUntilFinished: Long) {
                thisRate -= 1
                if (thisRate == 0) {
                    Projectile()
                    if (rateSpawn > 20)
                        rateSpawn -= 1
                    thisRate = rateSpawn
                }
            }

            override fun onFinish() {
                start()
            }
        }

        board.doOnPreDraw {
            spawnTimer.start()
        }

        return view
    }

    override fun onDetach() {
        spawnTimer.cancel()
        super.onDetach()
    }

    private fun shootWeapon() {
        bullets -= 1
        lostBullet()
        if (isLeftWeaponShoot) {
            isLeftWeaponShoot = false
            leftWeapon.animate().rotation(0F)
                .translationX(0F)
                .setDuration(1L)
                .translationY(0F)
                .withEndAction {
                    leftWeapon.animate()
                        .rotationBy(-15F)
                        .setDuration(100L)
                        .translationXBy(-35F)
                        .translationYBy(20F)
                        .withEndAction {
                            leftWeapon.animate()
                                .rotationBy(15F)
                                .setDuration(100L)
                                .translationXBy(35F)
                                .translationYBy(-20F)
                                .start()
                        }
                        .start()
                }
                .start()

        } else {
            isLeftWeaponShoot = true
            rightWeapon.animate().rotation(0F)
                .translationX(0F)
                .setDuration(1L)
                .translationY(0F)
                .withEndAction {
                    rightWeapon.animate()
                        .rotationBy(15F)
                        .setDuration(100L)
                        .translationXBy(35F)
                        .translationYBy(20F)
                        .withEndAction {
                            rightWeapon.animate()
                                .rotationBy(-15F)
                                .setDuration(100L)
                                .translationXBy(-35F)
                                .translationYBy(-20F)
                                .start()
                        }
                        .start()
                }
                .start()
        }
    }

    private fun lostBullet() {
        when(bullets){
            7 -> bulletsList[0].visibility = View.INVISIBLE
            6 -> bulletsList[1].visibility = View.INVISIBLE
            5 -> bulletsList[2].visibility = View.INVISIBLE
            4 -> bulletsList[3].visibility = View.INVISIBLE
            3 -> bulletsList[4].visibility = View.INVISIBLE
            2 -> bulletsList[5].visibility = View.INVISIBLE
            1 -> bulletsList[6].visibility = View.INVISIBLE
            0 -> {bulletsList[7].visibility = View.INVISIBLE
            reload()}
        }
    }

    private fun reload(bulletId:Int = 0) {
        isCanFire = false
        bulletsList[bulletId].animate()
            .alpha(1F)
            .setDuration(100L)
            .withStartAction { bulletsList[bulletId].visibility = View.VISIBLE }
            .withEndAction {
                bullets += 1
                if (bulletId==7){
                    isCanFire = true
                }
                else{
                    reload(bulletId = bulletId+1)
                }
            }
            .start()
    }

    inner class Projectile {

        private val imageView: ImageView
        private val imageId: Int = images.random()
        private val timerMovement: CountDownTimer
        private val speed: Int

        init {
            imageView = createImageView()
            timerMovement = createTimerMovement()
            speed = ((board.height / 350F).toInt()..(board.height / 80F).toInt()).random() + 1
            board.addView(imageView)
            timerMovement.start()
            allProjectile.add(this)
        }

        private fun createImageView(): ImageView {
            val imageView = ImageView(requireContext())
            val size = ((board.height / 6)..(board.height / 3)).random()
            val params = FrameLayout.LayoutParams(size, size)
            imageView.setImageResource(imageId)
            imageView.layoutParams = params
            imageView.x = (0..(board.width - size)).random().toFloat()
            imageView.y = (-1 * size).toFloat()
            imageView.setOnClickListener {
                touchProjectile()
            }
            imageView.animate()
                .setDuration(1L)
                .rotationBy((0..360).random().toFloat())
                .start()
            return imageView
        }

        fun stop(){
            timerMovement.cancel()
        }

        private fun createTimerMovement(): CountDownTimer {
            return object : CountDownTimer(16, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    if (imageView.y >= board.height) {
                        cancel()
                        projectileDown()
                    } else {
                        imageView.y += speed
                    }
                }

                override fun onFinish() {
                    start()
                }
            }
        }

        private fun touchProjectile() {
            if (!isCanFire)
                return
            shootWeapon()
            timerMovement.cancel()
            if (imageId == R.drawable.item_5) {
                loseLive()
            } else {
                countBonus += 1
                val reward = getReward()
                if (reward != 1)
                    animateReward(reward)
                score += reward
                scoreText.text = score.toString()
            }
            board.removeView(imageView)
            allProjectile.remove(this)
        }

        private fun getReward(): Int {
            if (countBonus % 100 == 0)
                return 1000
            else
                if (countBonus % 50 == 0)
                    return 250
                else
                    if (countBonus % 25 == 0)
                        return 50
                    else
                        if (countBonus % 10 == 0)
                            return 10
                        else
                            return 1
        }

        private fun projectileDown() {
            if (imageId != R.drawable.item_5) {
                loseLive()
            }
            board.removeView(imageView)
            allProjectile.remove(this@Projectile)
        }

        private fun loseLive() {
            if (countBonus <10)
                countBonus = 0
            else
                countBonus -= 10
            live -= 1
            when (live) {
                2 -> livesList[2].visibility = View.INVISIBLE
                1 -> livesList[1].visibility = View.INVISIBLE
                0 -> {livesList[0].visibility = View.INVISIBLE
                gameOver()}
            }
        }
    }

    private fun animateReward(reward: Int) {
        bonus.animate()
            .translationY((-1F * board.height / 2))
            .alphaBy(0F)
            .setDuration(1L)
            .withEndAction {
                bonus.animate()
                    .withStartAction {
                        bonus.visibility = View.VISIBLE
                        when (reward) {
                            10 -> bonus.setImageResource(R.drawable.x10)
                            50 -> bonus.setImageResource(R.drawable.x50)
                            250 -> bonus.setImageResource(R.drawable.x250)
                            1000 -> bonus.setImageResource(R.drawable.x1000)
                        }
                    }
                    .translationYBy((1F * board.height / 2))
                    .alphaBy(1F)
                    .setDuration(1000L)
                    .withEndAction {
                        bonus.animate()
                            .alpha(0F)
                            .setDuration(700L)
                            .withEndAction {
                                bonus.visibility = View.INVISIBLE
                            }
                            .start()
                    }
                    .start()
            }
            .start()
    }

    private fun gameOver(){
        isCanFire = false
        spawnTimer.cancel()
        allProjectile.forEach { it.stop() }

        val animAlphaOne = AnimationUtils.loadAnimation(requireContext(),R.anim.alpha_one_result)
        animAlphaOne.setAnimationListener(object:Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {
                resultScoreText.text = score.toString()
                resultLayout.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation?) {

            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
        resultLayout.startAnimation(animAlphaOne)
    }
}