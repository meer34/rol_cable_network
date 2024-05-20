package rcn.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import rcn.web.model.Bucket;
import rcn.web.model.Channel;
import rcn.web.model.ChannelPackage;
import rcn.web.repo.BucketRepo;
import rcn.web.repo.ChannelRepo;
import rcn.web.repo.PackageRepo;
import rcn.web.specification.EntitySpecification;

@Service
public class SubscriptionService {

	@Autowired private PackageRepo packageRepo;
	@Autowired private BucketRepo bucketRepo;
	@Autowired private ChannelRepo channelRepo;
	
//	EntitySpecification<ChannelPackage> spec = new EntitySpecification<ChannelPackage>();
	
	public ChannelPackage savePackage(ChannelPackage pack) {
		return packageRepo.save(pack);
	}
	
	public ChannelPackage getPackageById(Long id) {
		return packageRepo.findById(id).orElse(null);
	}
	
	public List<ChannelPackage> getAllPackages() {
		return packageRepo.findAll();
	}
	
	public Page<ChannelPackage> getAllPackages(Integer pageNo, Integer pageSize) {
		return packageRepo.findAll(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}

	public void deletePackageById(Long id) {
		packageRepo.deleteById(id);
	}

	public Page<ChannelPackage> searchPackageByKeyword(String keyword, int pageNo, Integer pageSize) {
		PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
		return packageRepo.findAll(Specification.where(EntitySpecification.textInAllStringColumns(keyword)), pageRequest);
	}
	
	public Bucket saveBucket(Bucket bucket) {
		return bucketRepo.save(bucket);
	}
	
	public Bucket getBucketById(Long id) {
		return bucketRepo.findById(id).orElse(null);
	}
	
	public Page<Bucket> getAllBuckets(Integer pageNo, Integer pageSize) {
		return bucketRepo.findAll(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}
	
	public List<Bucket> getAllBuckets() {
		return bucketRepo.findAll();
	}

	public void deleteBucketById(Long id) {
		bucketRepo.deleteById(id);
	}

	public Page<Bucket> searchBucketByKeyword(String keyword, int pageNo, Integer pageSize) {
		PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
		return bucketRepo.findAll(Specification.where(EntitySpecification.textInAllStringColumns(keyword)), pageRequest);
	}
	
	public Channel saveChannel(Channel channel) {
		return channelRepo.save(channel);
	}
	
	public Channel getChannelById(Long id) {
		return channelRepo.findById(id).orElse(null);
	}
	
	public Page<Channel> getAllChannels(Integer pageNo, Integer pageSize) {
		return channelRepo.findAll(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}
	
	public List<Channel> getAllChannels() {
		return channelRepo.findAll();
	}

	public void deleteChannelById(Long id) {
		channelRepo.deleteById(id);
	}

	public Page<Channel> searchChannelByKeyword(String keyword, int pageNo, Integer pageSize) {
		PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
		return channelRepo.findAll(Specification.where(EntitySpecification.textInAllStringColumns(keyword)), pageRequest);
	}

}
