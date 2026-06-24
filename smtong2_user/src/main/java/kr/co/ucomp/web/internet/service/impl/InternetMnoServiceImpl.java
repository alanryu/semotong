package kr.co.ucomp.web.internet.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.ucomp.web.internet.entity.InternetMnoEntity;
import kr.co.ucomp.web.internet.mapper.InternetMnoMapper;
import kr.co.ucomp.web.internet.service.InternetMnoService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InternetMnoServiceImpl implements InternetMnoService {

	private InternetMnoMapper internetMnoMapper;

	@Override
	public List<InternetMnoEntity> list() {
		return internetMnoMapper.list();
	}

	@Override
	public Long count() {
		return internetMnoMapper.count();
	}

	
	@Override
	public List<InternetMnoEntity> listNew() {
		return internetMnoMapper.listNew();
	}

	@Override
	public Long countNew() {
		return internetMnoMapper.countNew();
	}
}
