package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Mapper
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(target = "start", expression = "java(booking.getStart())")
    @Mapping(target = "end", expression = "java(booking.getEnd())")
    @Mapping(target = "id", expression = "java(booking.getId())")
    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "start", expression = "java(booking.getStart())")
    @Mapping(target = "end", expression = "java(booking.getEnd())")
    @Mapping(target = "id", expression = "java(booking.getId())")
    List<BookingDto> toBooksDto(List<Booking> books);
}
