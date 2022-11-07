package br.com.thecodeforce.recognize.demo.rest.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.thecodeforce.recognize.demo.executor.RecognitionGoogleExecutor;
import br.com.thecodeforce.recognize.demo.model.TranscriptionDTO;
import br.com.thecodeforce.recognize.demo.rest.RecognizeResurcesInterface;
import io.swagger.annotations.Api;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/v1/recognize")
@Api(tags = "Recognize", consumes = "multipart/form-data,application/json", produces = MediaType.APPLICATION_JSON_VALUE)
public class RecognizeResurcesImpl implements RecognizeResurcesInterface {

	@Autowired
	private RecognitionGoogleExecutor recognitionGoogleExecutor;

	@Override
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public Mono<TranscriptionDTO> recognize(@RequestPart("voice_data") Mono<FilePart> voiceData) {
		return recognitionGoogleExecutor.execute(voiceData);
	}

}
