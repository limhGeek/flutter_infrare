package com.limh.flutter_infrare

import java.util.*

/**
 * @author limh
 * @function
 * @date 2020/6/5 15:03
 */
internal object NecPattern {
    //引导码
    private const val startH = 9000
    private const val startL = 4500

    //结束码
    private const val endL = 560
    private const val endH = 2000

    //高电平
    private const val high8 = 560

    //低电平0：1125
    private const val low0 = 565

    //低电平1：2250
    private const val low1 = 1690
    private val list: MutableList<Int> = ArrayList()

    /**
     * 正常发码：引导码（9ms+4.5ms）+用户编码（高八位）+用户编码（低八位）+键数据码+键数据反码+结束码
     */
    fun buildPattern(userCodeH: Int, userCodeL: Int, keyCode: Int): IntArray {
        //用户编码高八位00
        val userH = constructBinaryCode(userCodeH)
        //用户编码低八位DF
        val userL = constructBinaryCode(userCodeL)
        //数字码
        val key = constructBinaryCode(keyCode)
        //数字反码
        val keyReverse = constructBinaryCode(keyCode.inv())
        list.clear()
        //引导码
        list.add(startH)
        list.add(startL)
        //用户编码
        changeAdd(userH)
        changeAdd(userL)
        //键数据码
        changeAdd(key)
        //键数据反码
        changeAdd(keyReverse)
        //结束码
        list.add(endL)
        list.add(endH)
        val size = list.size
        val pattern = IntArray(size)
        for (i in 0 until size) {
            pattern[i] = list[i]
        }
        return pattern
    }

    /**
     * 十六进制键值转化为二进制串，并逆转编码
     *
     * @param keyCode
     * @return
     */
    private fun constructBinaryCode(keyCode: Int): String {
        val binaryStr = convertToBinary(keyCode)
        val chars = binaryStr.toCharArray()
        val sb = StringBuffer()
        for (i in 7 downTo 4) {
            sb.append(chars[i])
        }
        for (i in 3 downTo 0) {
            sb.append(chars[i])
        }
        return sb.toString()
    }

    /**
     * 数字转换为长度为8位的二进制字符串
     *
     * @return
     */
    private fun convertToBinary(num: Int): String {
        val binary = Integer.toBinaryString(num)
        val sb8 = StringBuffer()
        //每个元素长度为8位，不够前面补充0
        return if (binary.length < 8) {
            for (i in 0 until 8 - binary.length) {
                sb8.append("0")
            }
            sb8.append(binary).toString()
        } else {
            binary.substring(binary.length - 8)
        }
    }

    /**
     * 二进制转成电平
     *
     * @param code
     */
    fun changeAdd(code: String) {
        val len = code.length
        var part: String
        for (i in 0 until len) {
            list.add(high8)
            part = code.substring(i, i + 1)
            if (part == "0") {
                list.add(low0)
            } else {
                list.add(low1)
            }
        }
    }
}