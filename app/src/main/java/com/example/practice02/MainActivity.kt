package com.example.practice02

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.practice02.databinding.ActivityEndBinding
import com.example.practice02.databinding.ActivityMainBinding
import com.example.practice02.databinding.ActivityStartBinding
import java.util.Timer
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    private var pNum = 3 //참가 인원수
    private var k = 1//참가자 번호 매기기
    private val pointList = mutableListOf<Float>() //비어있는 리스트

    private var isBlind: Boolean = false

    private lateinit var startBinding: ActivityStartBinding
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var endBinding: ActivityEndBinding

    @SuppressLint("SetTextI18n")
    private fun start(){
        startBinding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(startBinding.root)

        startBinding.btnBlindMode.setOnClickListener{
            isBlind = !isBlind
            if(isBlind){
                startBinding.btnBlindMode.text = "Blind Mode ON"
            }
            else{
                startBinding.btnBlindMode.text = "Blind Mode OFF"
            }
        }
        startBinding.tvNumber.text = pNum.toString()

        startBinding.btnMinus.setOnClickListener{
            pNum--
            if(pNum == 0){
                pNum = 1
            }
            startBinding.tvNumber.text = pNum.toString()
        }
        startBinding.btnPlus.setOnClickListener{
            pNum++
            if(pNum >= 10){
                pNum = 10
            }
            startBinding.tvNumber.text = pNum.toString()
        }
        startBinding.btnMainStart.setOnClickListener {
            main()
        }
    }
    @SuppressLint("SetTextI18n")
    fun main(){
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        var stage = 1 //isRunning을 지우고 stage1,2,3...으로 전환, stage 1은 초기값
        var sec = 0 //기본 단위가 mmsec이므로 나누기 100을 해야 1초임
        var timerTask: Timer? = null  //nullable로 선언

        val colorList = mutableListOf("#32E9321E", "#32E98E1E", "#32E9C41E")
        var colorIndex = k%3-1
        if(colorIndex == -1) {
            colorIndex = 2
        }

        //배경색 후보군 리스트 만들고 참가자별 색 지정하기
        val colorSel = colorList[colorIndex]

        //배경 색 지정 후 바꾸기
        mainBinding.backgroundMain.setBackgroundColor(Color.parseColor(colorSel))

        //랜덤 수 생성하기 박스 변수
        val randomBox = java.util.Random() //변수
        val num = randomBox.nextInt(1001) //11을 넣으면 0부터 10까지 정수로 반환
        mainBinding.tvRandom.text = (num.toFloat() / 100).toString()
        mainBinding.btnMainStart.text = "시작"
        mainBinding.tvPeople.text = "참가자 $k"

        mainBinding.btnBack.setOnClickListener{
            //초기화 및 처음부터 다시 시작
            pointList.clear()
            k = 1
            start()
        }

        mainBinding.btnMainStart.setOnClickListener {
            stage++ //클릭 시 stage가 올라감
            if (stage == 2) {
                timerTask = timer(period = 10) { // period가 1000 mmsecond = 1초마다 함수가 돌아간다
                    //period가 10으로 설정, sec/100로 설정, sec의 자료형을 Int에서 float으로 변경 시 소수점까지 나오게 실행 가능
                    sec++
                    //UI를 바꾸기 위해서는 runOnUiThread를 사용해야 한다.
                    runOnUiThread { //실시간으로 화면을 바꾸기 위해 사용
                        if (!isBlind) {
                            mainBinding.tvStartName.text = (sec.toFloat() / 100).toString() //+자료형 바꿔주기
                        } else if (isBlind && stage == 2){
                            mainBinding.tvStartName.text = "???"
                        }
                    }
                    //println(sec)  //초가 흘러갈때마다 시스템 상 출력
                }
                mainBinding.btnMainStart.text = "정지"
            }
            else if(stage == 3){ //정지한 이후에는 stage가 3으로 바뀜
                mainBinding.tvStartName.text = (sec.toFloat() / 100).toString() //멈출 때는 멈춰진 시간 표시
                timerTask?.cancel()  //nullable
                val point = kotlin.math.abs(sec - num).toFloat() / 100 // 절댓값 abs
                //포인트 리스트에 포인트 추가하기
                pointList.add(point)
                mainBinding.tvNumber.text = point.toString()
                mainBinding.btnMainStart.text = "다음"
                stage = 0
            }
            else if(stage == 1){
                if(k < pNum) { //총 참가자보다 k가 작아야 함
                    k++ //참가자 번호 증가
                    main()
                }
                else
                    end()
            }

        }
    }

    @SuppressLint("SetTextI18n")
    fun end(){
        endBinding = ActivityEndBinding.inflate(layoutInflater)
        setContentView(endBinding.root)

        endBinding.tvResult.text = (pointList.maxOrNull()).toString()
        val indexLast = pointList.indexOf(pointList.maxOrNull())
        endBinding.tvResultName.text = "참가자 " + (indexLast+1).toString()

        endBinding.btnRetry.setOnClickListener{
            //리스트 초기화 하기
            pointList.clear()
            k = 1

            start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        start()
    }
}
