import { useState } from 'react';
import { setProductFollowing } from '../services/dashboardService';

export function useToggleFollow() {
    const [isToggling, setIsToggling] = useState(false);

    const toggle = async (productId, currentIsFollowing, onSuccess) => {
        if (!productId || isToggling) return;
        try {
            setIsToggling(true);
            const safeCurrentStatus = Boolean(currentIsFollowing);
            const nextStatus = !safeCurrentStatus;
            
            await setProductFollowing(productId, nextStatus);
            
            if (onSuccess) {
                onSuccess(nextStatus);
            }
        } catch (error) {
            console.error("Takip işlemi başarısız:", error);
            alert("Takip durumu güncellenirken bir hata oluştu: " + error.message);
        } finally {
            setIsToggling(false);
        }
    };

    return { toggle, isToggling };
}