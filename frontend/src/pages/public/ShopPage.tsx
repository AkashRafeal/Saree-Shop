import React, { useState, useEffect, useMemo, useRef } from 'react';
import { useSearchParams } from 'react-router-dom';
import { ChevronLeft, ChevronRight, Sparkles, ChevronDown, Check } from 'lucide-react';
import { Product, Category } from '@/types';
import { ProductCard } from '@/components/product/ProductCard';
import api from '@/services/api';

interface CategoryMetadata {
  name: string;
  subtitle: string;
  badge: string;
  categoryId?: number;
  fabric?: string;
  occasion?: string;
  isNewArrival?: boolean;
}

const CATEGORY_DIRECTORY: Record<string, CategoryMetadata> = {
  'new-arrivals': {
    name: 'New Arrivals',
    subtitle: 'Newly unboxed heirloom silks and contemporary artisanal drapes, freshly woven for the season',
    badge: 'NEW ARRIVALS',
    isNewArrival: true,
  },
  'kanchipuram-silk': {
    name: 'Silk Sarees & Kanchipuram',
    subtitle: 'Authentic Silk Mark certified pure mulberry silk with rich gold temple zari borders',
    badge: 'SILK MARK CERTIFIED',
    categoryId: 1,
  },
  'banarasi-silk': {
    name: 'Royal Banarasi Brocades',
    subtitle: 'Handcrafted in Varanasi with opulent gold kadwa floral jaal & celebratory brocades',
    badge: 'ROYAL WEAVES',
    categoryId: 2,
  },
  'bridal-sarees': {
    name: 'Wedding & Bridal Collections',
    subtitle: 'Auspicious heirloom bridal trousseau, heavy zari drapes & grand celebratory edits',
    badge: 'BRIDAL TROUSSEAU',
    categoryId: 3,
  },
  'chanderi-organza': {
    name: 'Chanderi & Sheer Organza',
    subtitle: 'Featherlight gossamer weaves detailed with delicate hand-embroidery & scalloped borders',
    badge: 'FEATHERLIGHT DRAPES',
    categoryId: 4,
  },
  'tussar-silk': {
    name: 'Tussar & Wild Silk',
    subtitle: 'Textured hand-reeled wild silk sarees celebrated for organic earthy sheen & Madhubani art',
    badge: 'RAW ORGANIC WEAVES',
    categoryId: 5,
  },
  'cotton-linen': {
    name: 'Cotton & Linen Weaves',
    subtitle: 'Breathable fine count handloom cottons, Jamdani muslins, and artisanal linen sarees',
    badge: 'ARTISANAL COTTON',
    categoryId: 6,
  },
  'suits-dresses': {
    name: 'Suits & Designer Dresses',
    subtitle: 'Artisanal Anarkalis, Shararas, Kurta Sets & Tailored Indo-Western Occasion Ensembles',
    badge: 'DESIGNER ATELIER',
    categoryId: 4,
  },
  'gowns': {
    name: 'Gowns & Evening Drapes',
    subtitle: 'Contemporary Cocktail, Reception & Red Carpet Silhouette Gowns with Regal Finishes',
    badge: 'COCKTAIL & GALA',
    occasion: 'Cocktail / Party',
  },
  'kurti': {
    name: 'Kurti & Tunic Edit',
    subtitle: 'Comfortable Handloom Cotton Kurtis, Long Tunics & Everyday Festive Wear',
    badge: 'DAILY FESTIVE',
    categoryId: 6,
  },
};

const SORT_OPTIONS = [
  { value: 'newest', label: 'Newest First' },
  { value: 'price_asc', label: 'Price: Low to High' },
  { value: 'price_desc', label: 'Price: High to Low' },
  { value: 'popular', label: 'Most Popular' },
  { value: 'rating', label: 'Highest Rated' },
];

export const ShopPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [sortDropdownOpen, setSortDropdownOpen] = useState(false);

  const dropdownRef = useRef<HTMLDivElement | null>(null);

  // Search & category params
  const search = searchParams.get('search') || '';
  const categorySlug = searchParams.get('category') || '';
  const sort = searchParams.get('sort') || 'newest';
  const page = parseInt(searchParams.get('page') || '0', 10);

  // Close dropdown on outside click
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
        setSortDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Fetch Categories on mount
  useEffect(() => {
    api.get('/categories')
      .then((res) => setCategories(res.data?.data || []))
      .catch((err) => console.error('Categories error:', err));
  }, []);

  // Compute Active Heading Details
  const pageHeader = useMemo(() => {
    if (search) {
      return {
        badge: 'SEARCH RESULTS',
        title: `Results for "${search}"`,
        subtitle: 'Handpicked artisanal creations matching your search query',
      };
    }
    if (categorySlug && CATEGORY_DIRECTORY[categorySlug]) {
      const meta = CATEGORY_DIRECTORY[categorySlug];
      return {
        badge: meta.badge,
        title: meta.name,
        subtitle: meta.subtitle,
      };
    }
    if (categorySlug) {
      const found = categories.find((c) => c.slug === categorySlug);
      if (found) {
        return {
          badge: 'HANDLOOM EDIT',
          title: found.name,
          subtitle: found.description || 'Authentic handloom creations directly from master weavers',
        };
      }
    }
    return {
      badge: 'HERITAGE COLLECTION',
      title: 'All Handcrafted Sarees',
      subtitle: 'Explore our complete heritage collection of pure silks, handlooms & designer drapes',
    };
  }, [search, categorySlug, categories]);

  // Fetch Products based on route category
  useEffect(() => {
    setLoading(true);
    let selectedCatId: number | undefined;
    let selectedFabric = '';
    let selectedOccasion = '';

    const isNewArrivalCategory = categorySlug === 'new-arrivals' || searchParams.get('category') === 'new-arrivals';

    if (categorySlug && !isNewArrivalCategory) {
      const meta = CATEGORY_DIRECTORY[categorySlug];
      if (meta) {
        if (meta.categoryId) selectedCatId = meta.categoryId;
        if (meta.fabric) selectedFabric = meta.fabric;
        if (meta.occasion) selectedOccasion = meta.occasion;
      } else {
        const found = categories.find((c) => c.slug === categorySlug);
        if (found) selectedCatId = found.id;
      }
    }

    const params: Record<string, any> = {
      page,
      size: 12,
      sort,
    };
    if (search) params.search = search;
    if (selectedCatId) params.categoryId = selectedCatId;
    if (selectedFabric) params.fabric = selectedFabric;
    if (selectedOccasion) params.occasion = selectedOccasion;
    if (isNewArrivalCategory) {
      params.isNewArrival = true;
    }

    api.get('/products', { params })
      .then((res) => {
        const data = res.data?.data;
        let prods = data?.content || [];

        // When on NEW ARRIVALS, strictly show ONLY newly arrived sarees
        if (isNewArrivalCategory) {
          const onlyNew = prods.filter((p: any) => p.isNewArrival === true || p.newArrival === true);
          setProducts(onlyNew);
          setTotalElements(onlyNew.length);
          setTotalPages(Math.max(1, Math.ceil(onlyNew.length / 12)));
          return;
        }

        // If specific niche filter returned 0, fallback gracefully to full catalog
        if (prods.length === 0) {
          api.get('/products', { params: { page: 0, size: 12, sort } })
            .then((fallbackRes) => {
              const fbData = fallbackRes.data?.data;
              setProducts(fbData?.content || []);
              setTotalElements(fbData?.totalElements || 0);
              setTotalPages(fbData?.totalPages || 1);
            })
            .catch(() => {
              setProducts([]);
            })
            .finally(() => setLoading(false));
          return;
        }

        setProducts(prods);
        setTotalElements(data?.totalElements || 0);
        setTotalPages(data?.totalPages || 1);
      })
      .catch((err) => {
        console.error('Products fetch error, attempting fallback:', err);
        if (isNewArrivalCategory) {
          api.get('/products/new-arrivals')
            .then((naRes) => {
              const naProds = naRes.data?.data || [];
              const onlyNew = naProds.filter((p: any) => p.isNewArrival === true || p.newArrival === true);
              setProducts(onlyNew);
              setTotalElements(onlyNew.length);
              setTotalPages(Math.max(1, Math.ceil(onlyNew.length / 12)));
            })
            .catch(() => {
              setProducts([]);
            })
            .finally(() => setLoading(false));
          return;
        }

        // Resilient fallback query for general catalog
        api.get('/products', { params: { page: 0, size: 12, sort: 'newest' } })
          .then((fbRes) => {
            const fbData = fbRes.data?.data;
            setProducts(fbData?.content || []);
            setTotalElements(fbData?.totalElements || 0);
            setTotalPages(fbData?.totalPages || 1);
          })
          .catch(() => {
            setProducts([]);
          })
          .finally(() => setLoading(false));
      })
      .finally(() => setLoading(false));
  }, [categories, search, categorySlug, sort, page, searchParams]);

  const updateParam = (key: string, value: string) => {
    const newParams = new URLSearchParams(searchParams);
    if (value) {
      newParams.set(key, value);
    } else {
      newParams.delete(key);
    }
    newParams.set('page', '0');
    setSearchParams(newParams);
  };

  const currentSortLabel = SORT_OPTIONS.find((s) => s.value === sort)?.label || 'Newest First';

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-8">
      {/* Title & Custom Theme Dropdown Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-stone-200 pb-6">
        <div>
          <span className="inline-flex items-center gap-1.5 text-[11px] font-bold uppercase tracking-[0.25em] text-[#D81B60]">
            <Sparkles className="w-3.5 h-3.5" />
            <span>{pageHeader.badge}</span>
          </span>
          <h1 className="font-serif text-2xl sm:text-3xl lg:text-4xl font-bold text-stone-900 mt-1.5">
            {pageHeader.title}
          </h1>
          <p className="text-xs sm:text-sm text-stone-600 mt-1 max-w-2xl">
            {pageHeader.subtitle}
          </p>
          <p className="text-[11px] font-semibold text-stone-400 mt-2">
            Showing {totalElements} authentic handloom creations
          </p>
        </div>

        {/* Custom Brand-Themed Sort Dropdown (No Windows Blue) */}
        <div className="relative self-start sm:self-center" ref={dropdownRef}>
          <div className="flex items-center space-x-2">
            <span className="text-xs text-stone-500 font-medium hidden sm:inline">
              Sort By:
            </span>
            <button
              type="button"
              onClick={() => setSortDropdownOpen(!sortDropdownOpen)}
              className="inline-flex items-center justify-between gap-3 bg-white border border-stone-300 hover:border-[#D81B60] focus:border-[#D81B60] text-stone-800 text-xs font-semibold rounded-full px-5 py-2.5 shadow-sm transition-all focus:outline-none min-w-[170px]"
            >
              <span>{currentSortLabel}</span>
              <ChevronDown
                className={`w-4 h-4 text-[#D81B60] transition-transform duration-200 ${
                  sortDropdownOpen ? 'rotate-180' : ''
                }`}
              />
            </button>
          </div>

          {/* Theme-Matching Popup Menu */}
          {sortDropdownOpen && (
            <div className="absolute right-0 mt-1.5 w-48 bg-white border border-stone-200 rounded-xl shadow-xl z-50 py-1 overflow-hidden animate-fadeIn">
              {SORT_OPTIONS.map((opt) => {
                const isSelected = opt.value === sort;
                return (
                  <button
                    key={opt.value}
                    type="button"
                    onClick={() => {
                      updateParam('sort', opt.value);
                      setSortDropdownOpen(false);
                    }}
                    className={`w-full flex items-center justify-between px-3.5 py-2.5 text-xs text-left transition-colors ${
                      isSelected
                        ? 'bg-[#D81B60] text-white font-semibold'
                        : 'text-stone-700 hover:bg-rose-50 hover:text-[#D81B60]'
                    }`}
                  >
                    <span>{opt.label}</span>
                    {isSelected && <Check className="w-3.5 h-3.5 text-white" />}
                  </button>
                );
              })}
            </div>
          )}
        </div>
      </div>

      {/* Full-Width Product Grid */}
      <main className="space-y-8">
        {loading ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {[1, 2, 3, 4, 5, 6, 7, 8].map((n) => (
              <div key={n} className="h-96 bg-stone-100 rounded-xl animate-pulse" />
            ))}
          </div>
        ) : products.length === 0 ? (
          <div className="text-center py-20 bg-white rounded-2xl border border-stone-200 p-8 space-y-4">
            <p className="font-serif text-2xl font-bold text-stone-800">
              No Items Found in this Collection
            </p>
            <p className="text-xs text-stone-500 max-w-md mx-auto">
              Please check back soon or explore our other signature handloom collections.
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {products.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        )}

        {/* Pagination Controls */}
        {totalPages > 1 && (
          <div className="flex items-center justify-center space-x-3 pt-6 border-t border-stone-200">
            <button
              disabled={page === 0}
              onClick={() => updateParam('page', (page - 1).toString())}
              className="w-9 h-9 rounded-full border border-stone-300 bg-white text-stone-700 hover:bg-stone-50 hover:border-[#D81B60] disabled:opacity-40 transition-colors flex items-center justify-center shadow-xs cursor-pointer"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            <span className="text-xs font-semibold text-stone-700">
              Page {page + 1} of {totalPages}
            </span>
            <button
              disabled={page >= totalPages - 1}
              onClick={() => updateParam('page', (page + 1).toString())}
              className="w-9 h-9 rounded-full border border-stone-300 bg-white text-stone-700 hover:bg-stone-50 hover:border-[#D81B60] disabled:opacity-40 transition-colors flex items-center justify-center shadow-xs cursor-pointer"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        )}
      </main>
    </div>
  );
};

export default ShopPage;
