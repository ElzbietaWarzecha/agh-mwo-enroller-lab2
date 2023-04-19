package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;

    @Autowired
    ParticipantService participantService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {
        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting) {
        if (meetingService.alreadyExist(meeting)) {
            return new ResponseEntity<String>(
                    "Unable to create. A meeting with title " + meeting.getTitle() + " and date " + meeting.getDate() + " already exist.",
                    HttpStatus.CONFLICT);
        }

        meetingService.add(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meetingService.delete(meeting);
        return new ResponseEntity<Participant>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@PathVariable("id") long id, @RequestBody Meeting updatedMeeting) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        if (updatedMeeting.getTitle() != null) {
            meeting.setTitle(updatedMeeting.getTitle());
        }
        if (updatedMeeting.getDescription() != null) {
            meeting.setDescription(updatedMeeting.getDescription());
        }
        if (updatedMeeting.getDate() != null) {
            meeting.setDate(updatedMeeting.getDate());
        }
        meetingService.update(meeting);
        return new ResponseEntity<Participant>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        Collection<Participant> participant = meeting.getParticipants();
        return new ResponseEntity<Collection<Participant>>(participant, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") long id, @RequestBody Participant participant) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null || participantService.findByLogin(participant.getLogin()) == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        if (meeting.participantEnrolledMeeting(participant)) {
            return new ResponseEntity<String>(
                    "A participant with login " + participant.getLogin() + " already enrolled to meeting '" + meeting.getTitle() + "'.",
                    HttpStatus.CONFLICT);
        }
        meetingService.addParticipantToMeeting(meeting, participant);

        return new ResponseEntity<Participant>(participant, HttpStatus.OK);
    }

    @RequestMapping(value = "/{meetingId}/{participantId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeParticipantFromMeeting(@PathVariable("meetingId") long meetingId, @PathVariable("participantId") String participantId) {
        Meeting meeting = meetingService.findById(meetingId);
        Participant participant = participantService.findByLogin(participantId);
        if (meeting == null || participant == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meetingService.removeParticipantFromMeeting(meeting, participant);
        return new ResponseEntity<Participant>(participant, HttpStatus.OK);
    }
}
