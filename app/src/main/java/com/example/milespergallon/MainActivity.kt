package com.example.milespergallon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.milespergallon.databinding.ActivityMainBinding
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    var interpreter: Interpreter? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var mean= floatArrayOf(5.477707f,195.31847f,104.86943f,2990.2517f,15.559236f, 75.89809f, 0.6242038f, 0.17834395f, 0.19745223f)
        var std= floatArrayOf(1.6997876f, 104.33159f, 38.096214f, 843.8986f, 2.7892299f, 3.6756425f, 0.48510087f, 0.38341305f, 0.39871183f)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        interpreter= Interpreter(loadModelFile(),null)
        val origin:Spinner=binding.spinner
        val arrayAdapter=ArrayAdapter(applicationContext,android.R.layout.simple_spinner_dropdown_item,arrayOf("USA","Europe","Japan"))
        origin.adapter=arrayAdapter
        binding.btn.setOnClickListener {
               val floats=Array(1){FloatArray(9)}
                floats[0][0]=(binding.cylinder.text.toString().toFloat()-mean[0])/std[0]
            floats[0][1]=(binding.displacement.text.toString().toFloat()-mean[1])/std[1]
            floats[0][2]=(binding.horsepower.text.toString().toFloat()-mean[2])/std[2]
            floats[0][3]=(binding.weight.text.toString().toFloat()-mean[3])/std[3]
            floats[0][4]=(binding.acc.text.toString().toFloat()-mean[4])/std[4]
            floats[0][5]=(binding.year.text.toString().toFloat()-mean[5])/std[5]
            when(origin.selectedItemPosition){
                0->{
                    floats[0][6]=(1-mean[6])/std[6]
                    floats[0][7]=(0-mean[7])/std[7]
                    floats[0][8]=(0-mean[8])/std[8]
                }
                1->{
                    floats[0][6]=(0-mean[6])/std[6]
                    floats[0][7]=(1-mean[7])/std[7]
                    floats[0][8]=(0-mean[8])/std[8]
                }
                2->{
                    floats[0][6]=(0-mean[6])/std[6]
                    floats[0][7]=(0-mean[7])/std[7]
                    floats[0][8]=(1-mean[8])/std[8]
                }
            }
            val res:Float=doInterference(floats)
            binding.textView.text=res.toString()
            }
    }
    private fun doInterference(input:Array<FloatArray>): Float {
        val output=Array(1){FloatArray(1)}
        interpreter!!.run(input, output)
        return output[0][0]

    }
    @Throws(IOException::class)
    private fun loadModelFile(): ByteBuffer {
        val assetFileDes=this.assets.openFd("automobile.tflite")
        val fileInputStream= FileInputStream(assetFileDes.fileDescriptor)
        val fileChannel=fileInputStream.channel
        val startOffset=assetFileDes.startOffset
        val length=assetFileDes.length
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,length)
    }
}