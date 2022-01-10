package com.itransition.obzor.repo;

import com.itransition.obzor.entity.Overview;
import com.itransition.obzor.entity.User;
import com.itransition.obzor.entity.dto.OverviewDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OverviewRepository extends JpaRepository<Overview, Long> {
	@Query("select new com.itransition.obzor.entity.dto.OverviewDTO(" +
			"   m, " +
			"   count(ml), " +
			"   sum(case when ml = :user then 1 else 0 end) > 0" +
			") " +
			"from Overview m left join m.likes ml " +
			"where m.title = :title " +
			"group by m")
	List<OverviewDTO> findByTitle(@Param("title") String title, @Param("user") User user);

	@Query("select new com.itransition.obzor.entity.dto.OverviewDTO(" +
			"   m, " +
			"   count(ml), " +
			"   sum(case when ml = :user then 1 else 0 end) > 0" +
			") " +
			"from Overview m left join m.likes ml " +
			"where m.id = :id " +
			"group by m")
	OverviewDTO findById(@Param("id") Long id, @Param("user") User user);

	@Query("select new com.itransition.obzor.entity.dto.OverviewDTO(" +
			"   m, " +
			"   count(ml), " +
			"   sum(case when ml = :user then 1 else 0 end) > 0" +
			") " +
			"from Overview m left join m.likes ml " +
			"group by m")
	List<OverviewDTO> findAll(@Param("user") User user);

	@Query("select p from Overview p where concat(p.title,p.description) like %?1% ")
	List<Overview> findAll(String keyword);
}
