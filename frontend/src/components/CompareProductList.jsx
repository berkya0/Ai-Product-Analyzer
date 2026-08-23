import React from 'react';
import CompareProductCard from './CompareProductCard';

export default function CompareProductList({ products, selectedProductIds = [], onSelectProduct }) {
  return (
    <div className="w-full max-w-sm p-4 bg-white border border-blue-200 rounded-2xl flex flex-col gap-3 min-h-[500px]">
      {products && products.length > 0 ? (
        products.map((product) => (
          <CompareProductCard
            key={product.id}
            product={product}
            onSelect={onSelectProduct}
            isSelected={selectedProductIds.includes(product.id)}
          />
        ))
      ) : (
        <div className="text-center text-sm text-gray-400 py-10">
          Karşılaştırılacak ürün bulunamadı.
        </div>
      )}
    </div>
  );
}