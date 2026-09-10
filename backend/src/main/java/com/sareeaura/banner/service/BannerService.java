package com.sareeaura.banner.service;

import com.sareeaura.banner.entity.Banner;
import com.sareeaura.banner.repository.BannerRepository;
import com.sareeaura.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    public List<Banner> getActiveBanners() {
        return bannerRepository.findByActiveTrueOrderByDisplayOrderAsc();
    }

    public List<Banner> getAllBanners() {
        List<Banner> list = bannerRepository.findAll();
        if (list.isEmpty()) {
            Banner b1 = Banner.builder()
                    .title("Royal Kanchipuram Silks")
                    .subtitle("Handwoven pure mulberry silk with gold zari motifs")
                    .imageUrl("https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=1600&q=80")
                    .ctaText("Explore Collection")
                    .targetUrl("/shop")
                    .displayOrder(1)
                    .active(true)
                    .build();
            Banner b2 = Banner.builder()
                    .title("Bridal & Festive Heirlooms")
                    .subtitle("Masterpiece sarees designed for timeless celebrations")
                    .imageUrl("https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=1600&q=80")
                    .ctaText("Discover Heritage")
                    .targetUrl("/shop")
                    .displayOrder(2)
                    .active(true)
                    .build();
            bannerRepository.saveAll(List.of(b1, b2));
            return bannerRepository.findAll();
        }
        return list;
    }

    public Banner createBanner(Banner banner) {
        return bannerRepository.save(banner);
    }

    public Banner updateBanner(Long id, Banner request) {
        Banner existing = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner", "id", id));
        if (request.getTitle() != null) existing.setTitle(request.getTitle());
        if (request.getSubtitle() != null) existing.setSubtitle(request.getSubtitle());
        if (request.getImageUrl() != null) existing.setImageUrl(request.getImageUrl());
        if (request.getCtaText() != null) existing.setCtaText(request.getCtaText());
        if (request.getTargetUrl() != null) existing.setTargetUrl(request.getTargetUrl());
        existing.setDisplayOrder(request.getDisplayOrder());
        existing.setActive(request.isActive());
        return bannerRepository.save(existing);
    }

    public void deleteBanner(Long id) {
        bannerRepository.deleteById(id);
    }
}
