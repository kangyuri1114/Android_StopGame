package com.example.practice02

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import java.lang.Math.abs
import java.util.Timer
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    var p_num = 3 //참가 인원수
    var k = 1//참가자 번호 매기기
    val point_list = mutableListOf<Float>() //비어있는 리스트

    var isBlind = false

    fun start(){
        setContentView(R.layout.activity_start)
        val tv_number: TextView = findViewById(R.id.tv_number)
        val btn_plus: Button = findViewById(R.id.btn_plus)
        val btn_minus: Button = findViewById(R.id.btn_minus)
        val btn_mainStart: Button = findViewById(R.id.btn_mainStart)
        val btn_BlindMode: Button = findViewById(R.id.btn_BlindMode)

        btn_BlindMode.setOnClickListener{
            isBlind = !isBlind
            if(isBlind == true){
                btn_BlindMode.text = "Blind Mode ON"
            }
            else{
                btn_BlindMode.text = "Blind Mode OFF"
            }
        }
        tv_number.text = p_num.toString()
        
        btn_minus.setOnClickListener{
            p_num--
            if(p_num == 0){
                p_num = 1
            }
            tv_number.text = p_num.toString()
        }
        btn_plus.setOnClickListener{
            p_num++
            if(p_num >= 10){
                p_num = 10
            }
            tv_number.text = p_num.toString()
        }
        btn_mainStart.setOnClickListener {
            main()
        }
    }
    fun main(){
        setContentView(R.layout.activity_main)//리소스의 레이아웃의 액티비티 메인 xml파일(화면)을 불러옴
        var stage = 1 //isRunning을 지우고 stage1,2,3...으로 전환, stage 1은 초기값
        var sec: Int = 0 //기본 단위가 mmsec이므로 나누기 100을 해야 1초임
        var timerTask: Timer? = null  //nullable로 선언
        val tv_t: TextView = findViewById(R.id.tv_StartName)
        val tv_r: TextView = findViewById(R.id.tv_random)
        val tv_p: TextView = findViewById(R.id.tv_number)
        val tv_people: TextView = findViewById(R.id.tv_people)

        val btn: Button = findViewById(R.id.btn_mainStart)
        val btn_Back: Button = findViewById(R.id.btn_Back)

        val background_main: ConstraintLayout = findViewById(R.id.background_main)

        val color_list = mutableListOf<String>("#32E9321E", "#32E98E1E", "#32E9C41E")
        var color_index = k%3-1
        if(color_index == -1) {
            color_index = 2
        }

        //배경색 후보군 리스트 만들고 참가자별 색 지정하기
          val color_sel = color_list.get(color_index)

        //배경 색 지정 후 바꾸기
        background_main.setBackgroundColor(Color.parseColor(color_sel))

        //랜덤 수 생성하기 박스 변수
        val random_box = java.util.Random() //변수
        val num = random_box.nextInt(1001) //11을 넣으면 0부터 10까지 정수로 반환
        tv_r.text = (num.toFloat() / 100).toString()
        btn.text = "시작"
        tv_people.text = "참가자 $k"

        btn_Back.setOnClickListener{
            //초기화 및 처음부터 다시 시작
            point_list.clear()
            k = 1
            start()
        }

        btn.setOnClickListener {
            stage++ //클릭 시 stage가 올라감
            if (stage == 2) {
                timerTask = timer(period = 10) { // period가 1000 mmsecond = 1초마다 함수가 돌아간다
                    //period가 10으로 설정, sec/100로 설정, sec의 자료형을 Int에서 float으로 변경 시 소수점까지 나오게 실행 가능
                    sec++
                    //UI를 바꾸기 위해서는 runOnUiThread를 사용해야 한다.
                    runOnUiThread { //실시간으로 화면을 바꾸기 위해 사용
                        if (isBlind == false) {
                            tv_t.text = (sec.toFloat() / 100).toString() //+자료형 바꿔주기
                        } else if (isBlind == true && stage == 2){
                            tv_t.text = "???"
                        }
                    }
                    //println(sec)  //초가 흘러갈때마다 시스템 상 출력
                }
                btn.text = "정지"
            }
            else if(stage == 3){ //정지한 이후에는 stage가 3으로 바뀜
                tv_t.text = (sec.toFloat() / 100).toString() //멈출 때는 멈춰진 시간 표시
                timerTask?.cancel()  //nullable
                val point = abs(sec-num).toFloat()/100 //절댓값 abs
                //포인트 리스트에 포인트 추가하기
                point_list.add(point)
                tv_p.text = point.toString()
                btn.text = "다음"
                stage = 0
            }
            else if(stage == 1){
                if(k < p_num) { //총 참가자보다 k가 작아야 함
                    k++ //참가자 번호 증가
                    main()
                }
                else
                    end()
            }

        }
    }

    fun end(){
        setContentView(R.layout.activity_end)
        val tv_ResultName: TextView = findViewById(R.id.tv_ResultName)
        val tv_Result: TextView = findViewById(R.id.tv_Result)
        val btn_Retry: Button = findViewById(R.id.btn_Retry)

        tv_Result.text = (point_list.maxOrNull()).toString()
        var index_last = point_list.indexOf(point_list.maxOrNull())
        tv_ResultName.text = "참가자 " + (index_last+1).toString()

        btn_Retry.setOnClickListener{
            //리스트 초기화 하기
            point_list.clear()
            k = 1

            start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        start()
    }
}



