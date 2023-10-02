package com.saber.helper;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


public class JalaliCalendar extends Calendar {
    public static int[] gregorianDaysInMonth = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    public static int[] jalaliDaysInMonth = new int[]{31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29};
    public static final int FARVARDIN = 0;
    public static final int ORDIBEHESHT = 1;
    public static final int KHORDAD = 2;
    public static final int TIR = 3;
    public static final int MORDAD = 4;
    public static final int SHAHRIVAR = 5;
    public static final int MEHR = 6;
    public static final int ABAN = 7;
    public static final int AZAR = 8;
    public static final int DEY = 9;
    public static final int BAHMAN = 10;
    public static final int ESFAND = 11;
    private static TimeZone timeZone = TimeZone.getDefault();
    private static boolean isTimeSeted = false;
    private static final int ONE_SECOND = 1000;
    private static final int ONE_MINUTE = 60000;
    private static final int ONE_HOUR = 3600000;
    private static final long ONE_DAY = 86400000L;
    static final int BCE = 0;
    static final int CE = 1;
    public static final int AD = 1;
    private GregorianCalendar cal;
    static final int[] MIN_VALUES = new int[]{0, 1, 0, 1, 0, 1, 1, 7, 1, 0, 0, 0, 0, 0, 0, -46800000, 0};
    static final int[] LEAST_MAX_VALUES = new int[]{1, 292269054, 11, 52, 4, 28, 365, 6, 4, 1, 11, 23, 59, 59, 999, 50400000, 1200000};
    static final int[] MAX_VALUES = new int[]{1, 292278994, 11, 53, 6, 31, 366, 6, 6, 1, 11, 23, 59, 59, 999, 50400000, 7200000};

    public JalaliCalendar() {
        this(TimeZone.getDefault(), Locale.getDefault());
    }

    public JalaliCalendar(TimeZone zone) {
        this(zone, Locale.getDefault());
    }

    public JalaliCalendar(Locale aLocale) {
        this(TimeZone.getDefault(), aLocale);
    }

    public JalaliCalendar(TimeZone zone, Locale aLocale) {
        this(zone, aLocale, (Date)null);
    }

    public JalaliCalendar(Date date) {
        this(TimeZone.getDefault(), Locale.getDefault(), date);
    }

    public JalaliCalendar(TimeZone zone, Locale aLocale, Date date) {
        super(zone, aLocale);
        timeZone = zone;
        Calendar calendar = Calendar.getInstance(zone, aLocale);
        if (date != null) {
            calendar.setTime(date);
        }

        JalaliCalendar.YearMonthDate yearMonthDate = new JalaliCalendar.YearMonthDate(calendar.get(1), calendar.get(2), calendar.get(5));
        yearMonthDate = gregorianToJalali(yearMonthDate);
        super.set(yearMonthDate.getYear(), yearMonthDate.getMonth(), yearMonthDate.getDate());
        this.complete();
    }

    public JalaliCalendar(int year, int month, int dayOfMonth) {
        this(year, month, dayOfMonth, 0, 0, 0, 0);
    }

    public JalaliCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        this(year, month, dayOfMonth, hourOfDay, minute, 0, 0);
    }

    public JalaliCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        this(year, month, dayOfMonth, hourOfDay, minute, second, 0);
    }

    public JalaliCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, int millis) {
        this.set(1, year);
        this.set(2, month);
        this.set(5, dayOfMonth);
        if (hourOfDay >= 12 && hourOfDay <= 23) {
            this.set(9, 1);
            this.set(10, hourOfDay - 12);
        } else {
            this.set(10, hourOfDay);
            this.set(9, 0);
        }

        this.set(11, hourOfDay);
        this.set(12, minute);
        this.set(13, second);
        this.set(14, millis);
        JalaliCalendar.YearMonthDate yearMonthDate = jalaliToGregorian(new JalaliCalendar.YearMonthDate(year, month, dayOfMonth));
        this.cal = new GregorianCalendar(yearMonthDate.getYear(), yearMonthDate.getMonth(), yearMonthDate.getDate(), hourOfDay, minute, second);
        this.time = this.cal.getTimeInMillis();
        isTimeSeted = true;
    }

    public static JalaliCalendar.YearMonthDate gregorianToJalali(JalaliCalendar.YearMonthDate gregorian) {
        gregorian.setYear(gregorian.getYear() - 1600);
        gregorian.setDate(gregorian.getDate() - 1);
        int gregorianDayNo = 365 * gregorian.getYear() + (int)Math.floor((double)((gregorian.getYear() + 3) / 4)) - (int)Math.floor((double)((gregorian.getYear() + 99) / 100)) + (int)Math.floor((double)((gregorian.getYear() + 399) / 400));

        int i;
        for(i = 0; i < gregorian.getMonth(); ++i) {
            gregorianDayNo += gregorianDaysInMonth[i];
        }

        if (gregorian.getMonth() > 1 && (gregorian.getYear() % 4 == 0 && gregorian.getYear() % 100 != 0 || gregorian.getYear() % 400 == 0)) {
            ++gregorianDayNo;
        }

        gregorianDayNo += gregorian.getDate();
        int jalaliDayNo = gregorianDayNo - 79;
        int jalaliNP = (int)Math.floor((double)(jalaliDayNo / 12053));
        jalaliDayNo %= 12053;
        int jalaliYear = 979 + 33 * jalaliNP + 4 * (jalaliDayNo / 1461);
        jalaliDayNo %= 1461;
        if (jalaliDayNo >= 366) {
            jalaliYear += (int)Math.floor((double)((jalaliDayNo - 1) / 365));
            jalaliDayNo = (jalaliDayNo - 1) % 365;
        }

        for(i = 0; i < 11 && jalaliDayNo >= jalaliDaysInMonth[i]; ++i) {
            jalaliDayNo -= jalaliDaysInMonth[i];
        }

        int jalaliDay = jalaliDayNo + 1;
        return new JalaliCalendar.YearMonthDate(jalaliYear, i, jalaliDay);
    }

    public static JalaliCalendar.DateTime gregorianToJalali(JalaliCalendar.DateTime gregorian) {
        gregorian.setYear(gregorian.getYear() - 1600);
        gregorian.setDay(gregorian.getDay() - 1);
        int gregorianDayNo = 365 * gregorian.getYear() + (int)Math.floor((double)((gregorian.getYear() + 3) / 4)) - (int)Math.floor((double)((gregorian.getYear() + 99) / 100)) + (int)Math.floor((double)((gregorian.getYear() + 399) / 400));

        int i;
        for(i = 0; i < gregorian.getMonth(); ++i) {
            gregorianDayNo += gregorianDaysInMonth[i];
        }

        if (gregorian.getMonth() > 1 && (gregorian.getYear() % 4 == 0 && gregorian.getYear() % 100 != 0 || gregorian.getYear() % 400 == 0)) {
            ++gregorianDayNo;
        }

        gregorianDayNo += gregorian.getDay();
        int jalaliDayNo = gregorianDayNo - 79;
        int jalaliNP = (int)Math.floor((double)(jalaliDayNo / 12053));
        jalaliDayNo %= 12053;
        int jalaliYear = 979 + 33 * jalaliNP + 4 * (jalaliDayNo / 1461);
        jalaliDayNo %= 1461;
        if (jalaliDayNo >= 366) {
            jalaliYear += (int)Math.floor((double)((jalaliDayNo - 1) / 365));
            jalaliDayNo = (jalaliDayNo - 1) % 365;
        }

        for(i = 0; i < 11 && jalaliDayNo >= jalaliDaysInMonth[i]; ++i) {
            jalaliDayNo -= jalaliDaysInMonth[i];
        }

        int jalaliDay = jalaliDayNo + 1;
        return new JalaliCalendar.DateTime(jalaliYear, i, jalaliDay, gregorian.hour, gregorian.minute, gregorian.second);
    }

    public static JalaliCalendar.YearMonthDate jalaliToGregorian(JalaliCalendar.YearMonthDate jalali) {
        jalali.setYear(jalali.getYear() - 979);
        jalali.setDate(jalali.getDate() - 1);
        int jalaliDayNo = 365 * jalali.getYear() + jalali.getYear() / 33 * 8 + (int)Math.floor((double)((jalali.getYear() % 33 + 3) / 4));

        int i;
        for(i = 0; i < jalali.getMonth(); ++i) {
            jalaliDayNo += jalaliDaysInMonth[i];
        }

        jalaliDayNo += jalali.getDate();
        int gregorianDayNo = jalaliDayNo + 79;
        int gregorianYear = 1600 + 400 * (int)Math.floor((double)(gregorianDayNo / 146097));
        gregorianDayNo %= 146097;
        boolean leap = true;
        if (gregorianDayNo >= 36525) {
            --gregorianDayNo;
            gregorianYear += 100 * (int)Math.floor((double)(gregorianDayNo / '躬'));
            gregorianDayNo %= 36524;
            if (gregorianDayNo >= 365) {
                ++gregorianDayNo;
            } else {
                leap = false;
            }
        }

        gregorianYear += 4 * (int)Math.floor((double)(gregorianDayNo / 1461));
        gregorianDayNo %= 1461;
        if (gregorianDayNo >= 366) {
            leap = false;
            --gregorianDayNo;
            gregorianYear += (int)Math.floor((double)(gregorianDayNo / 365));
            gregorianDayNo %= 365;
        }

        for(i = 0; gregorianDayNo >= gregorianDaysInMonth[i] + (i == 1 && leap ? i : 0); ++i) {
            gregorianDayNo -= gregorianDaysInMonth[i] + (i == 1 && leap ? i : 0);
        }

        int gregorianDay = gregorianDayNo + 1;
        return new JalaliCalendar.YearMonthDate(gregorianYear, i, gregorianDay);
    }

    public static JalaliCalendar.DateTime jalaliToGregorian(JalaliCalendar.DateTime jalali) {
        jalali.setYear(jalali.getYear() - 979);
        jalali.setDay(jalali.getDay() - 1);
        int jalaliDayNo = 365 * jalali.getYear() + jalali.getYear() / 33 * 8 + (int)Math.floor((double)((jalali.getYear() % 33 + 3) / 4));

        int i;
        for(i = 0; i < jalali.getMonth(); ++i) {
            jalaliDayNo += jalaliDaysInMonth[i];
        }

        jalaliDayNo += jalali.getDay();
        int gregorianDayNo = jalaliDayNo + 79;
        int gregorianYear = 1600 + 400 * (int)Math.floor((double)(gregorianDayNo / 146097));
        gregorianDayNo %= 146097;
        boolean leap = true;
        if (gregorianDayNo >= 36525) {
            --gregorianDayNo;
            gregorianYear += 100 * (int)Math.floor((double)(gregorianDayNo / '躬'));
            gregorianDayNo %= 36524;
            if (gregorianDayNo >= 365) {
                ++gregorianDayNo;
            } else {
                leap = false;
            }
        }

        gregorianYear += 4 * (int)Math.floor((double)(gregorianDayNo / 1461));
        gregorianDayNo %= 1461;
        if (gregorianDayNo >= 366) {
            leap = false;
            --gregorianDayNo;
            gregorianYear += (int)Math.floor((double)(gregorianDayNo / 365));
            gregorianDayNo %= 365;
        }

        for(i = 0; gregorianDayNo >= gregorianDaysInMonth[i] + (i == 1 && leap ? i : 0); ++i) {
            gregorianDayNo -= gregorianDaysInMonth[i] + (i == 1 && leap ? i : 0);
        }

        int gregorianDay = gregorianDayNo + 1;
        return new JalaliCalendar.DateTime(gregorianYear, i, gregorianDay, jalali.getHour(), jalali.getMinute(), jalali.second);
    }

    public static int weekOfYear(int dayOfYear, int year) {
        switch(dayOfWeek(jalaliToGregorian(new JalaliCalendar.YearMonthDate(year, 0, 1)))) {
            case 2:
                ++dayOfYear;
                break;
            case 3:
                dayOfYear += 2;
                break;
            case 4:
                dayOfYear += 3;
                break;
            case 5:
                dayOfYear += 4;
                break;
            case 6:
                dayOfYear += 5;
                break;
            case 7:
                --dayOfYear;
        }

        dayOfYear = (int)Math.floor((double)(dayOfYear / 7));
        return dayOfYear + 1;
    }

    public static int dayOfWeek(JalaliCalendar.YearMonthDate yearMonthDate) {
        Calendar cal = new GregorianCalendar(yearMonthDate.getYear(), yearMonthDate.getMonth(), yearMonthDate.getDate());
        return cal.get(7);
    }

    public static boolean isLeepYear(int year) {
        return year % 33 == 1 || year % 33 == 5 || year % 33 == 9 || year % 33 == 13 || year % 33 == 17 || year % 33 == 22 || year % 33 == 26 || year % 33 == 30;
    }

    protected void computeTime() {
        if (!this.isTimeSet && !isTimeSeted) {
            Calendar cal = GregorianCalendar.getInstance(timeZone);
            if (!this.isSet(11)) {
                super.set(11, cal.get(11));
            }

            if (!this.isSet(10)) {
                super.set(10, cal.get(10));
            }

            if (!this.isSet(12)) {
                super.set(12, cal.get(12));
            }

            if (!this.isSet(13)) {
                super.set(13, cal.get(13));
            }

            if (!this.isSet(14)) {
                super.set(14, cal.get(14));
            }

            if (!this.isSet(15)) {
                super.set(15, cal.get(15));
            }

            if (!this.isSet(16)) {
                super.set(16, cal.get(16));
            }

            if (!this.isSet(9)) {
                super.set(9, cal.get(9));
            }

            if (this.internalGet(11) >= 12 && this.internalGet(11) <= 23) {
                super.set(9, 1);
                super.set(10, this.internalGet(11) - 12);
            } else {
                super.set(10, this.internalGet(11));
                super.set(9, 0);
            }

            JalaliCalendar.YearMonthDate yearMonthDate = jalaliToGregorian(new JalaliCalendar.YearMonthDate(this.internalGet(1), this.internalGet(2), this.internalGet(5)));
            cal.set(yearMonthDate.getYear(), yearMonthDate.getMonth(), yearMonthDate.getDate(), this.internalGet(11), this.internalGet(12), this.internalGet(13));
            this.time = cal.getTimeInMillis();
        } else if (!this.isTimeSet && isTimeSeted) {
            if (this.internalGet(11) >= 12 && this.internalGet(11) <= 23) {
                super.set(9, 1);
                super.set(10, this.internalGet(11) - 12);
            } else {
                super.set(10, this.internalGet(11));
                super.set(9, 0);
            }

            this.cal = new GregorianCalendar();
            super.set(15, timeZone.getRawOffset());
            super.set(16, timeZone.getDSTSavings());
            JalaliCalendar.YearMonthDate yearMonthDate = jalaliToGregorian(new JalaliCalendar.YearMonthDate(this.internalGet(1), this.internalGet(2), this.internalGet(5)));
            this.cal.set(yearMonthDate.getYear(), yearMonthDate.getMonth(), yearMonthDate.getDate(), this.internalGet(11), this.internalGet(12), this.internalGet(13));
            this.time = this.cal.getTimeInMillis();
        }

    }

    public void set(int field, int value) {
        switch(field) {
            case 2:
                super.set(field, value % 12);
                if (value > 11) {
                    this.add(field, value - 11);
                } else if (value < -11) {
                    this.add(field, value + 11);
                }
                break;
            case 3:
                if (this.isSet(1) && this.isSet(2) && this.isSet(5)) {
                    this.add(field, value - this.get(3));
                } else {
                    super.set(field, value);
                }
                break;
            case 4:
                if (this.isSet(1) && this.isSet(2) && this.isSet(5)) {
                    this.add(field, value - this.get(4));
                } else {
                    super.set(field, value);
                }
                break;
            case 5:
                super.set(field, 0);
                this.add(field, value);
                break;
            case 6:
                if (this.isSet(1) && this.isSet(2) && this.isSet(5)) {
                    super.set(1, this.internalGet(1));
                    super.set(2, 0);
                    super.set(5, 0);
                    this.add(field, value);
                } else {
                    super.set(field, value);
                }
                break;
            case 7:
                if (this.isSet(1) && this.isSet(2) && this.isSet(5)) {
                    this.add(7, value % 7 - this.get(7));
                } else {
                    super.set(field, value);
                }
                break;
            case 8:
            case 9:
            default:
                super.set(field, value);
                break;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
                if (this.isSet(1) && this.isSet(2) && this.isSet(5) && this.isSet(10) && this.isSet(11) && this.isSet(12) && this.isSet(13) && this.isSet(14)) {
                    this.cal = new GregorianCalendar();
                    JalaliCalendar.YearMonthDate yearMonthDate = jalaliToGregorian(new JalaliCalendar.YearMonthDate(this.internalGet(1), this.internalGet(2), this.internalGet(5)));
                    this.cal.set(yearMonthDate.getYear(), yearMonthDate.getMonth(), yearMonthDate.getDate(), this.internalGet(11), this.internalGet(12), this.internalGet(13));
                    this.cal.set(field, value);
                    yearMonthDate = gregorianToJalali(new JalaliCalendar.YearMonthDate(this.cal.get(1), this.cal.get(2), this.cal.get(5)));
                    super.set(1, yearMonthDate.getYear());
                    super.set(2, yearMonthDate.getMonth());
                    super.set(5, yearMonthDate.getDate());
                    super.set(11, this.cal.get(11));
                    super.set(12, this.cal.get(12));
                    super.set(13, this.cal.get(13));
                } else {
                    super.set(field, value);
                }
        }

    }

    protected void computeFields() {
        boolean temp = this.isTimeSet;
        if (!this.areFieldsSet) {
            this.setMinimalDaysInFirstWeek(1);
            this.setFirstDayOfWeek(7);
            int dayOfYear = 0;

            for(int index = 0; index < this.fields[2]; dayOfYear += jalaliDaysInMonth[index++]) {
            }

            dayOfYear += this.fields[5];
            super.set(6, dayOfYear);
            super.set(7, dayOfWeek(jalaliToGregorian(new JalaliCalendar.YearMonthDate(this.fields[1], this.fields[2], this.fields[5]))));
            if (0 < this.fields[5] && this.fields[5] < 8) {
                super.set(8, 1);
            }

            if (7 < this.fields[5] && this.fields[5] < 15) {
                super.set(8, 2);
            }

            if (14 < this.fields[5] && this.fields[5] < 22) {
                super.set(8, 3);
            }

            if (21 < this.fields[5] && this.fields[5] < 29) {
                super.set(8, 4);
            }

            if (28 < this.fields[5] && this.fields[5] < 32) {
                super.set(8, 5);
            }

            super.set(3, weekOfYear(this.fields[6], this.fields[1]));
            super.set(4, weekOfYear(this.fields[6], this.fields[1]) - weekOfYear(this.fields[6] - this.fields[5], this.fields[1]) + 1);
            this.isTimeSet = temp;
        }

    }

    public void add(int field, int amount) {
        if (field == 2) {
            amount += this.get(2);
            int newYear = amount >= 0 ? amount / 12 : amount / 12 - 1;
            int newMonth = amount >= 0 ? amount % 12 : amount - newYear * 12;
            this.add(1, newYear);
            super.set(2, newMonth);
            if (this.get(5) > jalaliDaysInMonth[newMonth]) {
                super.set(5, jalaliDaysInMonth[newMonth]);
                if (this.get(2) == 11 && isLeepYear(this.get(1))) {
                    super.set(5, 30);
                }
            }

            this.complete();
        } else if (field == 1) {
            super.set(1, this.get(1) + amount);
            if (this.get(5) == 30 && this.get(2) == 11 && !isLeepYear(this.get(1))) {
                super.set(5, 29);
            }

            this.complete();
        } else {
            JalaliCalendar.YearMonthDate yearMonthDate = jalaliToGregorian(new JalaliCalendar.YearMonthDate(this.get(1), this.get(2), this.get(5)));
            Calendar gc = new GregorianCalendar(yearMonthDate.getYear(), yearMonthDate.getMonth(), yearMonthDate.getDate(), this.get(11), this.get(12), this.get(13));
            gc.add(field, amount);
            yearMonthDate = gregorianToJalali(new JalaliCalendar.YearMonthDate(gc.get(1), gc.get(2), gc.get(5)));
            super.set(1, yearMonthDate.getYear());
            super.set(2, yearMonthDate.getMonth());
            super.set(5, yearMonthDate.getDate());
            super.set(11, gc.get(11));
            super.set(12, gc.get(12));
            super.set(13, gc.get(13));
            this.complete();
        }

    }

    public void roll(int field, boolean up) {
        this.roll(field, up ? 1 : -1);
    }

    public void roll(int field, int amount) {
        if (amount != 0) {
            if (field >= 0 && field < 15) {
                this.complete();
                int[] var10000;
                int d;
                int unit;
                int index;
                switch(field) {
                    case 1:
                        super.set(1, this.internalGet(1) + amount);
                        if (this.internalGet(2) == 11 && this.internalGet(5) == 30 && !isLeepYear(this.internalGet(1))) {
                            super.set(5, 29);
                        }
                    case 3:
                        break;
                    case 4:
                    case 8:
                    default:
                        throw new IllegalArgumentException();
                    case 5:
                        unit = 0;
                        if (0 <= this.get(2) && this.get(2) <= 5) {
                            unit = 31;
                        }

                        if (6 <= this.get(2) && this.get(2) <= 10) {
                            unit = 30;
                        }

                        if (this.get(2) == 11) {
                            if (isLeepYear(this.get(1))) {
                                unit = 30;
                            } else {
                                unit = 29;
                            }
                        }

                        d = (this.get(5) + amount) % unit;
                        if (d < 0) {
                            d += unit;
                        }

                        super.set(5, d);
                        break;
                    case 6:
                        index = isLeepYear(this.internalGet(1)) ? 366 : 365;
                        d = (this.internalGet(6) + amount) % index;
                        d = d > 0 ? d : d + index;
                        int month = 0;

                        int temp;
                        for(temp = 0; d > temp; temp += jalaliDaysInMonth[month++]) {
                        }

                        --month;
                        super.set(2, month);
                        super.set(5, jalaliDaysInMonth[this.internalGet(2)] - (temp - d));
                        break;
                    case 7:
                        index = amount % 7;
                        if (index < 0) {
                            index += 7;
                        }

                        for(d = 0; d != index; ++d) {
                            if (this.internalGet(7) == 6) {
                                this.add(5, -6);
                            } else {
                                this.add(5, 1);
                            }
                        }

                        return;
                    case 9:
                        if (amount % 2 != 0) {
                            if (this.internalGet(9) == 0) {
                                this.fields[9] = 1;
                            } else {
                                this.fields[9] = 0;
                            }

                            if (this.get(9) == 0) {
                                super.set(11, this.get(10));
                            } else {
                                super.set(11, this.get(10) + 12);
                            }
                        }
                        break;
                    case 10:
                        super.set(10, (this.internalGet(10) + amount) % 12);
                        if (this.internalGet(10) < 0) {
                            var10000 = this.fields;
                            var10000[10] += 12;
                        }

                        if (this.internalGet(9) == 0) {
                            super.set(11, this.internalGet(10));
                        } else {
                            super.set(11, this.internalGet(10) + 12);
                        }
                        break;
                    case 11:
                        this.fields[11] = (this.internalGet(11) + amount) % 24;
                        if (this.internalGet(11) < 0) {
                            var10000 = this.fields;
                            var10000[11] += 24;
                        }

                        if (this.internalGet(11) < 12) {
                            this.fields[9] = 0;
                            this.fields[10] = this.internalGet(11);
                        } else {
                            this.fields[9] = 1;
                            this.fields[10] = this.internalGet(11) - 12;
                        }
                    case 2:
                        index = (this.internalGet(2) + amount) % 12;
                        if (index < 0) {
                            index += 12;
                        }

                        super.set(2, index);
                        d = jalaliDaysInMonth[index];
                        if (this.internalGet(2) == 11 && isLeepYear(this.internalGet(1))) {
                            d = 30;
                        }

                        if (this.internalGet(5) > d) {
                            super.set(5, d);
                        }
                        break;
                    case 12:
                        unit = 60;
                        d = (this.internalGet(12) + amount) % unit;
                        if (d < 0) {
                            d += unit;
                        }

                        super.set(12, d);
                        break;
                    case 13:
                        unit = 60;
                        d = (this.internalGet(13) + amount) % unit;
                        if (d < 0) {
                            d += unit;
                        }

                        super.set(13, d);
                        break;
                    case 14:
                         unit = 1000;
                        d = (this.internalGet(14) + amount) % unit;
                        if (d < 0) {
                            d += unit;
                        }

                        super.set(14, d);
                }

            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    public int getMinimum(int field) {
        return MIN_VALUES[field];
    }

    public int getMaximum(int field) {
        return MAX_VALUES[field];
    }

    public int getGreatestMinimum(int field) {
        return MIN_VALUES[field];
    }

    public int getLeastMaximum(int field) {
        return LEAST_MAX_VALUES[field];
    }

    public static void main(String[] args) {
    }

    public static class YearMonthDate {
        private int year;
        private int month;
        private int date;

        public YearMonthDate(int year, int month, int date) {
            this.year = year;
            this.month = month;
            this.date = date;
        }

        public int getYear() {
            return this.year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return this.month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDate() {
            return this.date;
        }

        public void setDate(int date) {
            this.date = date;
        }

        public String toString() {
            int var10000 = this.getYear();
            return var10000 + "/" + this.getMonth() + "/" + this.getDate();
        }

        public String getYearMonthDay() {
            String monthString = "";
            String dayString = "";
            int var10000;
            if (this.getMonth() + 1 < 10) {
                var10000 = this.getMonth();
                monthString = "0" + (var10000 + 1);
            } else {
                var10000 = this.getMonth();
                monthString = (var10000 + 1).makeConcatWithConstants<invokedynamic>(var10000 + 1);
            }

            if (this.getDate() < 10) {
                dayString = "0" + this.getDate();
            } else {
                dayString = this.getDate().makeConcatWithConstants<invokedynamic>(this.getDate());
            }

            return this.getYear() + monthString + dayString;
        }

        public String getYearMonthDayBySeprator(String dateSeprator) {
            String monthString = "";
            String dayString = "";
            int var10000;
            if (this.getMonth() + 1 < 10) {
                var10000 = this.getMonth();
                monthString = "0" + (var10000 + 1);
            } else {
                var10000 = this.getMonth();
                monthString = (var10000 + 1).makeConcatWithConstants<invokedynamic>(var10000 + 1);
            }

            if (this.getDate() < 10) {
                dayString = "0" + this.getDate();
            } else {
                dayString = this.getDate().makeConcatWithConstants<invokedynamic>(this.getDate());
            }

            return this.getYear() + dateSeprator + monthString + dateSeprator + dayString;
        }

        public String getYearDayMonthBySeprator(String dateSeprator) {
            String monthString = "";
            String dayString = "";
            int var10000;
            if (this.getMonth() + 1 < 10) {
                var10000 = this.getMonth();
                monthString = "0" + (var10000 + 1);
            } else {
                var10000 = this.getMonth();
                monthString = (var10000 + 1).makeConcatWithConstants<invokedynamic>(var10000 + 1);
            }

            if (this.getDate() < 10) {
                dayString = "0" + this.getDate();
            } else {
                dayString = this.getDate().makeConcatWithConstants<invokedynamic>(this.getDate());
            }

            return this.getYear() + dateSeprator + dayString + dateSeprator + monthString;
        }
    }

    public static class DateTime {
        private int year;
        private int month;
        private int day;
        private int hour;
        private int minute;
        private int second;

        public DateTime(int year, int month, int day, int hour, int minute, int second) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }

        public int getYear() {
            return this.year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return this.month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return this.day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getHour() {
            return this.hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMinute() {
            return this.minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        public int getSecond() {
            return this.second;
        }

        public void setSecond(int second) {
            this.second = second;
        }

        public String toString() {
            return "'" + this.getDateTimeBySeprator("-", " ", ":") + "'";
        }

        public String getDateTimeBySeprator(String dateSeprator, String dateTimeSeprator, String timeSeprator) {
            String monthString = "";
            String dayString = "";
            String hourString = "";
            String minuteString = "";
            String secondString = "";
            if (this.getMonth() + 1 < 10) {
                int var10000 = this.getMonth();
                monthString = "0" + (var10000 + 1);
            } else {
                monthString = monthString + (this.getMonth() + 1);
            }

            if (this.getDay() < 10) {
                dayString = "0" + this.getDay();
            } else {
                dayString = dayString + this.getDay();
            }

            if (this.getHour() < 10) {
                hourString = "0" + this.getHour();
            } else {
                hourString = hourString + this.getHour();
            }

            if (this.getMinute() < 10) {
                minuteString = "0" + this.getMinute();
            } else {
                minuteString = minuteString + this.getMinute();
            }

            if (this.getSecond() < 10) {
                secondString = "0" + this.getSecond();
            } else {
                secondString = secondString + this.getSecond();
            }

            return this.getYear() + dateSeprator + monthString + dateSeprator + dayString + dateTimeSeprator + hourString + timeSeprator + minuteString + timeSeprator + secondString;
        }
    }
}
