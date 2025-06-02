package com.example.shieldus.repository.problem;

import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.entity.problem.ProblemTestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemTestCaseRepository extends JpaRepository<ProblemTestCase, Long> {

    List<ProblemTestCase> findByProblem(Problem problem);

    boolean existsByIdAndProblem_id(Long id, Long problem_id);

    List<ProblemTestCase> findByProblem_Id(Long problemId);

    List<ProblemTestCase> findByProblem_IdAndIsTestCaseIsTrue(Long problemId);
}
