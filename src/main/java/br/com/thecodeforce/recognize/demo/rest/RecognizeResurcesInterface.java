package br.com.thecodeforce.recognize.demo.rest;

import org.springframework.http.codec.multipart.FilePart;

import br.com.thecodeforce.recognize.demo.model.TranscriptionDTO;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@OpenAPIDefinition(info = @Info(title = "Recognize", description = "API de reconhecimento de voz.", version = "1.0.0"))
@Tag(name = "Recognize API")
public interface RecognizeResurcesInterface {

	@Operation(summary = "Recognize", description = "Speech-to-Text")
	Mono<TranscriptionDTO> recognize(Mono<FilePart> dataStream);

}
