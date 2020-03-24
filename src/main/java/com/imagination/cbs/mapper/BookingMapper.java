package com.imagination.cbs.mapper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.imagination.cbs.domain.Booking;
import com.imagination.cbs.domain.BookingRevision;
import com.imagination.cbs.dto.BookingDto;

@Mapper(componentModel = "spring")
public interface BookingMapper {

	@Mapping(source = "changedDate", target = "changedDate", qualifiedByName = "stringToTimeStamp", ignore = true)
	public Booking toBookingDomainFromBookingDto(BookingDto bookingDto);

	public BookingDto toBookingDtoFromBooking(Booking bookingDto);

	@Named("stringToTimeStamp")
	public static Timestamp stringToTimeStampConverter(String date) {
		Timestamp timeStamp = null;
		SimpleDateFormat parseDate = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date parsedDate = parseDate.parse(date);
			timeStamp = new java.sql.Timestamp(parsedDate.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeStamp;
	}

	@Named("timeStampToString")
	public static String timeStampToStringConverter(Timestamp timeStamp) {
		return timeStamp.toString();
	}

	@Named("longToString")
	public static String longToStringConverter(String domainValue) {
		return domainValue.toString();
	}

	@Mapping(source = "contractedFromDate", target = "contractedFromDate", qualifiedByName = "timeStampToString")
	@Mapping(source = "contractedToDate", target = "contractedToDate", qualifiedByName = "timeStampToString")
	public BookingDto toBookingDtoFromBookingRevision(BookingRevision bookingRevisionDto);

	@Mapping(source = "changedDate", target = "changedDate", qualifiedByName = "stringToTimeStamp", ignore = true)
	@Mapping(source = "contractedFromDate", target = "contractedFromDate", qualifiedByName = "stringToTimeStamp")
	@Mapping(source = "contractedToDate", target = "contractedToDate", qualifiedByName = "stringToTimeStamp")
	@Mapping(source = "contractorSignedDate", target = "contractorSignedDate", qualifiedByName = "stringToTimeStamp")
	public BookingRevision toBookingRevisionFromBookingDto(BookingDto bookingDto);

}