package com.raybit.newvendor.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import com.raybit.newvendor.data.AppConstants

import com.raybit.newvendor.utils.DateTimeUtils.DateFormat.UTC_FORMAT
import java.text.DateFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

object DateTimeUtils {

    val utcFormat = SimpleDateFormat(DateFormat.UTC_FORMAT_NORMAL, Locale.getDefault())
    /**
     * get difference between current time and provided timezone
     *
     * @return differrence in time between two timestamp
     */
    val timeOffset: Long
        get() {
            val currentTime = System.currentTimeMillis()
            val edtOffset = TimeZone.getTimeZone("Your Time Zone").getOffset(currentTime)
            val current = TimeZone.getDefault().getOffset(currentTime)
            return (current - edtOffset).toLong()
        }
    @RequiresApi(Build.VERSION_CODES.O)
    fun dateTimeFormatFromUTC(format: String, createdDate: String?): String {
        return if (createdDate == null || createdDate.isEmpty())
            ""
        else {
            try {
                val formatter = DateTimeFormatter.ISO_INSTANT
                val instant = Instant.from(formatter.parse(createdDate))

                val timeZone = TimeZone.getTimeZone("Etc/UTC")
                val fmt = java.text.SimpleDateFormat(format, Locale.getDefault())
                fmt.timeZone = timeZone

                fmt.format(Date.from(instant))
            } catch (e: ParseException) {
                // Handle parsing exception
                ""
            }
        }
    }
    fun timeDifferences(appointDate: String): Long {
        val df: SimpleDateFormat = SimpleDateFormat(UTC_FORMAT)
        //Desired format: 24 hour format: Change the pattern as per the need
        //Desired format: 24 hour format: Change the pattern as per the need
        val outputformat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date: Date? = null
        var output: String? = null
        try {
            //Converting the input String to Date
            date = df.parse(appointDate)
            //Changing the format of date and storing it in String
            if (appointDate.contains("pm") && date.hours!=12) {
                date!!.hours = date.hours + 12
            }
            val currentDate = Date()
            output = outputformat.format(date)
            val currentD: String = outputformat.format(currentDate)
            val diff = date!!.time - currentDate.time
            println(diff)
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            println(minutes)
            //Displaying the date
            println(output + currentD)
            return minutes
        } catch (pe: ParseException) {
            pe.printStackTrace()
        }
        return 0;
    }
    fun getTimeOffset(): String {
        return SimpleDateFormat(
            "ZZZZZ",
            timeLocale
        ).format(System.currentTimeMillis())
    }

    val currentDate: String
        get() {
            val cDate = Date()
            return SimpleDateFormat("yyyy-MM-dd", timeLocale).format(cDate)
        }
    val currentDateTime: String
        get() {
            val cDate = Date()
            return SimpleDateFormat("yyyy-MM-dd HH:mm a", timeLocale).format(cDate)
        }


    val dayName: String
        get() {
            val calendar = Calendar.getInstance()

            calendar.time = Date()

            val days = arrayOf(
                "SUNDAY",
                "MONDAY",
                "TUESDAY",
                "WEDNESDAY",
                "THURSDAY",
                "FRIDAY",
                "SATURDAY"
            )

            return days[calendar.get(Calendar.DAY_OF_WEEK)]
        }

    /**
     * get date form timestamp
     *
     * @param timestamp time to be converter
     * @param format    for date conversion eg(dd MMMM yyyy)
     * @return date in string
     */


    fun formatMillsToDate(mills: Long): String {
        return SimpleDateFormat("yyyy-MM-dd", timeLocale).format(mills)
    }

    fun parseDateToMills(date: String): Long? {
        return SimpleDateFormat("yyyy-MM-dd", timeLocale).parse(date)?.time
    }

    fun convertOrderOneDateToAnother(
        dateToConvert: String,
        formatFrom: String,
        formatTo: String
    ): String? {
        var outputDateStr: String? = null
        val inputFormat = SimpleDateFormat(formatFrom, Locale.ENGLISH)
        // inputFormat.timeZone = TimeZone.getDefault()
        val outputFormat = SimpleDateFormat(formatTo, Locale.ENGLISH)
        outputFormat.timeZone = TimeZone.getDefault()
        val date: Date
        try {
            date = inputFormat.parse(dateToConvert) ?: Date()
            outputDateStr = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return outputDateStr
    }

    fun getDateFromTimestamp(timestamp: String, format: String): String {


        val calendar = Calendar.getInstance()
        calendar.timeInMillis = (timestamp).toLong()

        return try {
            val sdf = SimpleDateFormat(format, timeLocale)
            val netDate = calendar.time
            sdf.format(netDate)
        } catch (ex: Exception) {
            "xx xxxx xxxx"
        }

    }

    /**
     * To convert a date to timestamp
     *
     * @param dateToConvert date to be converted
     * @param dateFormat    format of date entered
     * @return timestamp in milliseconds
     */

    fun convertDateToTimeStamp(dateToConvert: String, dateFormat: String): Long {
        val formatter = SimpleDateFormat(dateFormat, timeLocale)
        var date: Date? = null
        try {
            date = formatter.parse(dateToConvert)
            return date!!.time
        } catch (e: ParseException) {
            e.printStackTrace()
            return 0
        }

    }

    fun differenceDate(current: Long, slot: Long): Long {
        val diff: Long = current - slot
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        return hours
    }

    /**
     * Convert date from one format to another
     *
     * @param dateToConvert date to be converted
     * @param formatFrom    the format of the date to be converted
     * @param formatTo      the format of date you want the output
     * @return date in string as per the entered formats
     */


    fun getCalendarFormat(dateToConvert: String, dateFormat: String): Calendar? {
        val inputFormat = SimpleDateFormat(dateFormat, timeLocale)
        var date: Date? = null
        try {
            date = inputFormat.parse(dateToConvert)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.time = date ?: Date()
        return calendar
    }

    fun getDateFormat(dateToConvert: String, dateFormat: String): Date? {
        val inputFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
        var date: Date? = null
        try {
            date = inputFormat.parse(dateToConvert)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date
    }

    @SuppressLint("SimpleDateFormat")
    fun getDatePlusTwo(date: String): String? {
        try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val dat = format.parse(date)
            val calendar = Calendar.getInstance()
            calendar.time = dat
            calendar.add(Calendar.DAY_OF_YEAR, +2)
            val newDate = calendar.time
            return SimpleDateFormat("EEEE yyyy-MM-dd").format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return null
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateMinusSix(date: String): String? {
        try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val dat = format.parse(date)
            val calendar = Calendar.getInstance()
            calendar.time = dat
            calendar.add(Calendar.DAY_OF_YEAR, -6)
            val newDate = calendar.time
            return SimpleDateFormat("yyyy-MM-dd").format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return null
    }


    @SuppressLint("SimpleDateFormat")
    fun getDatePlusOne(date: String): String? {
        try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val dat = format.parse(date)
            val calendar = Calendar.getInstance()
            calendar.time = dat
            calendar.add(Calendar.DAY_OF_YEAR, +1)
            val newDate = calendar.time
            return SimpleDateFormat("EEEE yyyy-MM-dd").format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return null
    }

    @SuppressLint("SimpleDateFormat")
    fun sevenDayBackDate(): String {
        val cDate = Date()
        val calendar = Calendar.getInstance()
        calendar.time = cDate
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val newDate = calendar.time
        return SimpleDateFormat("yyyy-MM-dd").format(newDate)
    }

    @SuppressLint("SimpleDateFormat")
    fun thirtyDayBackDate(): String {
        val cDate = Date()
        val calendar = Calendar.getInstance()
        calendar.time = cDate
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val newDate = calendar.time
        return SimpleDateFormat("yyyy-MM-dd").format(newDate)
    }

    @SuppressLint("SimpleDateFormat")
    fun toDate(): String {
        val cDate = Date()
        val calendar = Calendar.getInstance()
        calendar.time = cDate
        calendar.add(Calendar.YEAR, -10)
        val newDate = calendar.time
        return SimpleDateFormat("yyyy-MM-dd").format(newDate)
    }

    fun nextDayOfWeek(dow: Int): Calendar {
        val date = Calendar.getInstance()
        var diff = dow - date.get(Calendar.DAY_OF_WEEK)
        if (diff <= 0) {
            diff += 7
        }
        date.add(Calendar.DAY_OF_MONTH, diff)
        return date
    }
    interface OnDateSelected {
        fun onDateSelected(date: String)
    }
    fun openDatePicker(activity: Activity, listener: OnDateSelected, max: Boolean, min: Boolean) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            activity,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                var selectedDate = "$dayOfMonth/${monthOfYear.plus(1)}/$year"

                selectedDate =
                    dateFormatChange(
                        DateFormat.DATE_FORMAT_SLASH_YEAR,
                        DateFormat.MON_YEAR_FORMAT,
                        selectedDate
                    )
                listener.onDateSelected(selectedDate)

            }, year, month, day
        )
        if (max)
            dpd.datePicker.maxDate = System.currentTimeMillis() - 36000
        if (min)
            dpd.datePicker.minDate = System.currentTimeMillis() - 36000

        dpd.show()
    }
    fun dateFormatChange(formatFrom: String, formatTo: String, value: String): String {
        val originalFormat = SimpleDateFormat(formatFrom, Locale.ENGLISH)
        val targetFormat = SimpleDateFormat(formatTo, Locale.ENGLISH)
        val date = originalFormat.parse(value)
        val formattedDate = targetFormat.format(date)
        return formattedDate
    }
    @SuppressLint("SimpleDateFormat")
    fun Format12to24(time: String): String {

        val displayFormat = SimpleDateFormat("HH:mm")
        val parseFormat = SimpleDateFormat("hh:mm aa")
        var date: Date? = null
        try {
            date = parseFormat.parse(time)
            return displayFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""
    }

    @SuppressLint("SimpleDateFormat")
    fun Format24to12(time: String): String {

        val displayFormat = SimpleDateFormat("HH:mm")
        val parseFormat = SimpleDateFormat("hh:mm aa")
        var date: Date? = null
        try {
            date = displayFormat.parse(time)
            return parseFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDate(dat: String): String? {
        var outputDateStr: String? = null
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd")
            val outputFormat = SimpleDateFormat("dd-MMM-yyyy")
            val date = inputFormat.parse(dat)
            outputDateStr = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return outputDateStr
    }

    fun getMonth(month: Int): String {
        return DateFormatSymbols().months[month]
    }

    @SuppressLint("SimpleDateFormat")
    fun getMonthNumber(month: String): Int {
        var date: Date? = null
        try {
            date = SimpleDateFormat("MMMM").parse(month)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val cal = Calendar.getInstance()
        cal.time = date
        return cal.get(Calendar.MONTH)
    }

    @SuppressLint("SimpleDateFormat")
    fun getMonthNumberInc(month: String): Int {
        var date: Date? = null
        try {
            date = SimpleDateFormat("MMMM").parse(month)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val cal = Calendar.getInstance()
        cal.time = date
        return cal.get(Calendar.MONTH) + 1
    }

    fun checkWeekOfMonday(): Boolean {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar[Calendar.DAY_OF_WEEK]

        return Calendar.MONDAY == dayOfWeek
    }

    val timeStampInSeconds: Long
        get() = System.currentTimeMillis() / 1000

    val timeLocale: Locale
        get() = Locale(if (AppConstants.LANG_CODE == "ar") "en" else AppConstants.LANG_CODE)

    fun getCurrentDate(): Calendar? {
        return Calendar.getInstance()
    }
    object DateFormat {
        const val DATE_FORMAT = "yyyy-MM-dd"
        const val DATE_TIME_FORMAT = "dd MMM yyyy · hh:mm a"
        const val TIME_FORMAT = "hh:mm a"
        const val TIME_FORMAT_24 = "HH:mm"
        const val MON_YEAR_FORMAT = "dd MMM, yyyy"
        const val MON_DAY_YEAR = "MMM dd, yyyy"
        const val MON_DATE = "MMM dd"
        const val DATE_FORMAT_SLASH = "MM/dd/yy"
        const val DATE_FORMAT_SLASH_YEAR = "dd/MM/yyyy"
        const val DATE_FORMAT_RENEW = "yyyy-MM-dd hh:mm"
        const val UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val UTC_FORMAT_NORMAL = "yyyy-MM-dd hh:mm:ss"
        const val MONTH_FORMAT = "MMM"
    }
}