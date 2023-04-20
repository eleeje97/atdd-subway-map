package subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.Line;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.repository.LineRepository;
import subway.repository.StationRepository;

import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRepository.save(createLine(lineRequest));
        return createLineResponse(line);
    }

    private Line createLine(LineRequest lineRequest) {
        Station upStation = findStationById(lineRequest.getUpStationId());
        Station downStation = findStationById(lineRequest.getDownStationId());

        return new Line(lineRequest.getName(),
                lineRequest.getColor(),
                upStation,
                downStation,
                lineRequest.getDistance());
    }

    private LineResponse createLineResponse(Line line) {
        return new LineResponse(line.getId(),
                line.getName(),
                line.getColor(),
                line.getStations());
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(() -> new NoSuchElementException("No such Station"));
    }
}