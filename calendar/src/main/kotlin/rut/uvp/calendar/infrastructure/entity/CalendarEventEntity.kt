package rut.uvp.calendar.infrastructure.entity

import jakarta.persistence.*
import kotlinx.datetime.Instant

@Entity
@Table(name = "calendar_events")
internal data class CalendarEventEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: String,
//    @Column(nullable = false, name = "end")
//    val end: Instant,
    @Column(nullable = false, name = "title")
    val title: String,
//    @Column(nullable = false, name = "start")
//    val start: Instant,
    @Column(nullable = false, name = "description")
    val description: String,
)
